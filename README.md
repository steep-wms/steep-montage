# Run the Montage workflow with Steep

This repository demonstrates how to run the
[Montage workflow](http://montage.ipac.caltech.edu/) with
[Steep](https://github.com/steep-wms/steep).

## Step 1: Download test data

Before you can run the workflow, you need to download some data first. The
`download_data.sh` script fetches files for a 2.8 square degrees mosaic of
2MASS images (J-, H-, and K-band) centred on
NGC 3372 ([Carina Nebula](https://en.wikipedia.org/wiki/Carina_Nebula)).

If you already have the [Montage toolkit](https://github.com/Caltech-IPAC/Montage)
and [aria2](https://aria2.github.io/) installed on your computer, you can simply run:

    ./download_data.sh

Otherwise, run the script inside a Docker container:

    docker run -it -v $(pwd)/data:/download/data \
      --entrypoint /download/download_data.sh \
      steep/steep-montage

*NOTE*: The dataset has a total size of about *1.3 GB*. The download will take
quite some time (depending on your Internet connection).

**IMPORTANT: the script deletes the contents of the `data` directory! Make sure
you know what you are doing.**

## Step 2: Run the Docker image

We created a Docker image that contains Steep and the Montage toolkit. Run the
image in the background with the following command:

    docker run --name steep-montage --rm -d -p 8080:8080 \
      -e STEEP_HTTP_HOST=0.0.0.0 -v $(pwd)/data:/data/montage \
      steep/steep-montage

Wait until Steep has started. You can either run `docker logs steep-montage`
to the see log output or go to <http://localhost:8080> to open Steep's web
interface.

## Step 3: Submit a workflow

We prepared the following workflows for you:

<dl>
<dt>workflow/montage.yaml</dt>
<dd>Calculates a greyscale image for the 2MASS J-band. Runs the Montage toolkit commands sequentially.</dd>
<dt>workflow/montage_parallel.yaml</dt>
<dd>A parallelized version of <code>workflow/montage.yaml</code> that can be used to run the Montage workflow in a distributed environment. Requires multiple Steep agents to be deployed to unfold its full potential in terms of performance compared to the sequential version, but can be run on a single machine as well.</dd>
<dt>workflow/montage_parallel_rgb.yaml</dt>
<dd>Calculates an RGB image based on the K-, H-, and J-bands. Parallelized version.</dd>
</dl>

Sumbit one of the workflows as follows:

    curl -X POST http://localhost:8080/workflows --data-binary @workflow/montage_parallel_rgb.yaml

Go to <http://localhost:8080/workflows> to open Steep's web interface and
monitor the progress of the workflow. Wait until it is completed.

## Step 4: Extract results

After the workflow has finished you can extract the final mosaic from the Docker
container:

    docker cp steep-montage:$(docker exec steep-montage find /tmp -name '*.jpg') .

It should look like this:

![Carina Nebula](result_rgb.jpg "Carina Nebula")

## Step 5: Stop Steep

Finally, run the following command to remove the Docker container:

    docker kill steep-montage

## License

The files in this repository are licensed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Montage License

The workflow uses the [Montage toolkit](https://github.com/Caltech-IPAC/Montage)
released under a [BSD 3-clause license](LICENSE_MONTAGE).
