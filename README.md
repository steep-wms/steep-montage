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
you downloaded and calculates a greyscale image for the 2MASS J-band.
`workflow/montage_parallel_rgb.json` calculates an RGB image based on the
K-, H-, and J-bands.

## Quick start

    ./download_data.sh
    docker build -t jobmanager3-montage .
    docker run --name jobmanager3-montage --rm -d -p 8080:8080 \
      -e JOBMANAGER_HTTP_HOST=0.0.0.0 -v $(pwd)/data:/data/montage \
      jobmanager3-montage

    ... wait

    curl -X POST http://localhost:8080/workflows --data @workflow/montage_parallel_rgb.json
    open http://localhost:8080/workflows

    ... wait

    docker cp jobmanager3-montage:$(docker exec jobmanager3-montage find /tmp -name '*.jpg') .
    docker kill jobmanager3-montage

![Carina Nebula](result_rgb.jpg "Carina Nebula")

License
-------

The files in this repository are licensed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Montage License
---------------

The workflow uses the [Montage toolkit](https://github.com/Caltech-IPAC/Montage)
released under a [BSD 3-clause license](LICENSE_MONTAGE).
