# Spark PBSPro Support
This adds support for PBS Professional HPC resource manager to Apache Spark

## How to:
In the spark project root, punch in these commands:
```bash
# Clone the repo
git clone https://github.com/UtkarshMe/Spark-PBSPro resource-managers/pbs

# Apply patches from the `patches` folder to spark (in the root directory).
git am resource-managers/pbs/patches/Add-pbs-resource-manager-as-a-module.patch

# Build!
build/mvn -DskipTests -Ppbs package
```
