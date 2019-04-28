#!/usr/bin/env node

// This program parses the stats files created by multiple calls to
// mDiffFitWrapper and concats them to a table that can be used by the
// subsequent Montage processes.
//
// Usage: ./mConcatFit.js <input_file_1> ... <input_file_n> <target_file>

const fs = require("fs").promises;
const sprintf = require("sprintf-js").sprintf;

if (process.argv.length < 4) {
  console.error("Usage: ./mConcatFit.js <input_file_1> ... <input_file_n> <target_file>")
  process.exit(1);
}

let inputFiles = process.argv.slice(2, process.argv.length - 1);
let targetFile = process.argv[process.argv.length - 1];

function parseStats(stats) {
  let pos = 0;

  if (stats.startsWith("[struct ")) {
    pos = 8;
  } else if (stats.startsWith("[array ")) {
    pos = 7;
  } else {
    throw "Stats must begin with `[struct ' or `[array '";
  }

  if (!stats.endsWith("]")) {
    throw "Stats must end with `]'";
  }

  let result = {};
  let quote = false;
  let last = "";
  let key;
  while (pos < stats.length) {
    let c = stats[pos];
    if (c === '\\') {
      if (quote) {
        pos++;
      }
      last += stats[pos];
    } else if (c === '"') {
      quote = !quote;
    } else if (!quote && c === '=') {
      key = last.trim();
      last = "";
    } else if (!quote && (c === ',' || c === ']')) {
      if (typeof key !== "undefined") {
        let value = last.trim();
        let n = parseFloat(value);
        if (!isNaN(n)) {
          value = n;
        }
        result[key] = value;
      }
      last = "";
      key = undefined;
    } else {
      last += c;
    }
    ++pos;
  }

  return result;
}

async function main() {
  let of = await fs.open(targetFile, "w");

  await of.write("| plus|minus|       a    |      b     |      c     | crpix1  | crpix2  | xmin | xmax | ymin | ymax | xcenter | ycenter |  npixel |    rms     |    boxx    |    boxy    |  boxwidth  | boxheight  |   boxang   |\n");

  for (let i of inputFiles) {
    let stats = (await fs.readFile(i, "utf-8")).trim();
    if (stats === "") {
      console.log(`Ignoring empty stat file \`${i}'`);
      continue;
    }

    let row;
    try {
      let ps = parseStats(stats);

      row = sprintf(" %5d %5d %12.5e %12.5e %12.5e %9.2f %9.2f %6d %6d %6d %6d %9.2f %9.2f %9.0f %12.5e %12.1f %12.1f %12.1f %12.1f %12.1f\n",
          ps["cntr1"], ps["cntr2"], ps["a"], ps["b"], ps["c"],
          ps["crpix1"], ps["crpix2"], ps["xmin"], ps["xmax"],
          ps["ymin"], ps["ymax"], ps["xcenter"], ps["ycenter"],
          ps["npixel"], ps["rms"], ps["boxx"], ps["boxy"],
          ps["boxwidth"], ps["boxheight"], ps["boxang"]);
    } catch (e) {
      console.log(`Invalid stats file \`${i}'`);
      throw e;
    }
    
    await of.write(row);
  }

  await of.close();
}

main().catch(err => {
  console.error(err);
  process.exit(1);
});
