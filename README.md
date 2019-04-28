# JobManager 3 and the Montage workflow

This repository contains everything you need to run the
[Montage workflow](http://montage.ipac.caltech.edu/) with the JobManager 3
(except for the data).

Have a look at the Dockerfile for details about how to configure the
environment and the JobManager.

## Download test data

In order to run the workflows from the `workflow` directory, you need to
download data first. The `download_data.sh` script fetches example data for
a 2.8 square degrees mosaic of 2MASS images (J-, H-, and K-band) centred on
NGC 3372 ([Carina Nebula](https://en.wikipedia.org/wiki/Carina_Nebula)).

NOTE: The download will take quite some time (30 minutes or so) and the
dataset has a total size of about 1.5 GB.

**IMPORTANT: the script deletes the `data` directory if it exists! Make sure
you know what you are doing.**

## Example workflows

`workflow/montage.json` and `workflow/montage_parallel.json` use the data that
you data downloaded with the instructions above and calculates a greyscale
image for the 2MASS J-band. You may extend the workflow to also calculate the
H-band and K-band to produce a colour image.
