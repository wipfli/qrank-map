## Usage

Steps on Ubuntu are the following:

Get the QRank file:

```
cd src/main/java/com/onthegomap/planetiler/examples
wget https://qrank.wmcloud.org/download/qrank.csv.gz
gzip -d qrank.csv.gz
```

Build Planetiler (Requires Java, see [Planetiler->Contributing](https://github.com/onthegomap/planetiler/blob/main/CONTRIBUTING.md):

```
./mvnw clean package --file standalone.pom.xml
```

Run Planetiler in the background:

```
screen -d -m "./runworld.sh"
```

Watch the logs:

```
tail -f logs.txt
```

Look at the tiles:

```
docker run --rm -it -v "$(pwd)/data":/data -p 8080:8080 maptiler/tileserver-gl -p 8080
```
