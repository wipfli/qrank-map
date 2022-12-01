package com.onthegomap.planetiler.examples;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.Profile;
import com.onthegomap.planetiler.config.Arguments;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.util.ZoomFunction;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class QRank implements Profile {
  static HashMap<String, String> qranks;

  public static void main(String[] args) throws Exception {
    run(Arguments.fromArgsOrConfigFile(args));
  }

  static void run(Arguments inArgs) throws Exception {
    qranks = new HashMap<String, String>();

    System.out.println("Start reading qrank.csv...");
    BufferedReader reader;
    reader = new BufferedReader(new FileReader("src/main/java/com/onthegomap/planetiler/examples/qrank.csv"));
    String line = reader.readLine();
    while (line != null) {
      String[] parts = line.split(",");
      qranks.put(parts[0].trim(), parts[1].trim());
      line = reader.readLine();
    }
    reader.close();

    var args = inArgs.orElse(Arguments.of(
      "minzoom", 0,
      "maxzoom", 9,
      "tile_warning_size_mb", 100
    ));
    String area = args.getString("area", "geofabrik area to download", "monaco");
    Planetiler.create(args)
      .setProfile(new QRank())
      .addOsmSource("osm",
        Path.of("data", "sources", area + ".osm.pbf"),
        "planet".equalsIgnoreCase(area) ? "aws:latest" : ("geofabrik:" + area)
      )
      .overwriteOutput("mbtiles", Path.of("data", "qrank.mbtiles"))
      .run();
  }

  static int getQRank(Object wikidata) {
    String qrank = qranks.get(wikidata.toString());
    if (qrank == null) {
      return 0;
    }
    else {
      return Integer.parseInt(qrank);
    }
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    if (sourceFeature.isPoint() && sourceFeature.hasTag("wikidata") && sourceFeature.hasTag("name"))
    {
      features.point("qrank")
        .setZoomRange(0, 9)
        .setSortKey(-getQRank(sourceFeature.getTag("wikidata")) / 100)
        .setPointLabelGridSizeAndLimit(
          12, // only limit at z12 and below
          32, // break the tile up into 32x32 px squares
          4 // any only keep the 4 nodes with lowest sort-key in each 32px square
        )
        .setBufferPixelOverrides(ZoomFunction.maxZoom(12, 32))
        .setAttr("name", sourceFeature.getTag("name"))
        .setAttr("@qrank", getQRank(sourceFeature.getTag("wikidata")));
    }
    if (sourceFeature.canBeLine() && sourceFeature.hasTag("boundary", "administrative") && sourceFeature.hasTag("admin_level")) {
      if (sourceFeature.hasTag("admin_level", "2")) {
        features.line("boundary-admin-2")
          .setZoomRange(0, 7)
          .setMinPixelSize(0);
      }
      if (sourceFeature.hasTag("admin_level", "4")) {
        features.line("boundary-admin-4")
          .setZoomRange(7, 7)
          .setMinPixelSize(0);
      }
    }
  }

  @Override
  public String name() {
    return "osm qrank";
  }

  @Override
  public String attribution() {
    return """
      <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>
      """.trim();
  }
}