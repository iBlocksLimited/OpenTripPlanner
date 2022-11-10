package org.opentripplanner.standalone.config.routerconfig.updaters;

import static org.opentripplanner.standalone.config.framework.json.OtpVersion.V2_2;

import java.util.ArrayList;
import java.util.Set;
import org.opentripplanner.ext.vehicleparking.hslpark.HslParkUpdaterParameters;
import org.opentripplanner.ext.vehicleparking.kml.KmlUpdaterParameters;
import org.opentripplanner.ext.vehicleparking.parkapi.ParkAPIUpdaterParameters;
import org.opentripplanner.standalone.config.framework.json.NodeAdapter;
import org.opentripplanner.updater.vehicle_parking.VehicleParkingSourceType;
import org.opentripplanner.updater.vehicle_parking.VehicleParkingUpdaterParameters;
import org.opentripplanner.util.OtpAppException;

public class VehicleParkingUpdaterConfig {

  public static VehicleParkingUpdaterParameters create(String updaterRef, NodeAdapter c) {
    var sourceType = c
      .of("sourceType")
      .since(V2_2)
      .summary("The source of the vehicle updates.")
      .asEnum(VehicleParkingSourceType.class);
    var feedId = c
      .of("feedId")
      .since(V2_2)
      .summary("The name of the data source.")
      .description("This will end up in the API responses as the feed id of of the parking lot.")
      .asString(null);
    var timeZone = c
      .of("timeZone")
      .since(V2_2)
      .summary("The time zone of the feed.")
      .description("Used for converting abstract opening hours into concrete points in time.")
      .asZoneId(null);
    return switch (sourceType) {
      case HSL_PARK -> new HslParkUpdaterParameters(
        updaterRef,
        c
          .of("facilitiesFrequencySec")
          .since(V2_2)
          .summary("How often the facilities should be updated.")
          .asInt(3600),
        c.of("facilitiesUrl").since(V2_2).summary("URL of the facilities.").asString(null),
        feedId,
        sourceType,
        c
          .of("utilizationsFrequencySec")
          .since(V2_2)
          .summary("How often the utilization should be updated.")
          .asInt(600),
        c.of("utilizationsUrl").since(V2_2).summary("URL of the utilization data.").asString(null),
        timeZone,
        c.of("hubsUrl").since(V2_2).summary("Hubs URL").asString(null)
      );
      case KML -> new KmlUpdaterParameters(
        updaterRef,
        c.of("url").since(V2_2).summary("URL of the KML file.").asString(null),
        feedId,
        c.of("namePrefix").since(V2_2).summary("Prefix for the names.").asString(null),
        c.of("frequencySec").since(V2_2).summary("How often to update the parking lots.").asInt(60),
        c.of("zip").since(V2_2).summary("Whether the resource is zip-compressed.").asBoolean(false),
        sourceType
      );
      case PARK_API, BICYCLE_PARK_API -> new ParkAPIUpdaterParameters(
        updaterRef,
        c.of("url").since(V2_2).summary("URL of the resource.").asString(null),
        feedId,
        c.of("frequencySec").since(V2_2).summary("How often to update the source.").asInt(60),
        c.of("headers").since(V2_2).summary("HTTP headers to add.").asStringMap(),
        new ArrayList<>(
          c.of("tags").since(V2_2).summary("Tags to add to the parking lots.").asStringSet(Set.of())
        ),
        sourceType,
        timeZone
      );
    };
  }
}