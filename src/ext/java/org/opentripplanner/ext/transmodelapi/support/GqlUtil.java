package org.opentripplanner.ext.transmodelapi.support;

import graphql.Scalars;
import graphql.introspection.Introspection.DirectiveLocation;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import java.time.ZoneId;
import java.util.List;
import org.opentripplanner.ext.transmodelapi.TransmodelRequestContext;
import org.opentripplanner.ext.transmodelapi.mapping.TransitIdMapper;
import org.opentripplanner.ext.transmodelapi.model.scalars.DateScalarFactory;
import org.opentripplanner.ext.transmodelapi.model.scalars.DateTimeScalarFactory;
import org.opentripplanner.ext.transmodelapi.model.scalars.DoubleFunctionScalarFactory;
import org.opentripplanner.ext.transmodelapi.model.scalars.DurationScalarFactory;
import org.opentripplanner.ext.transmodelapi.model.scalars.LocalTimeScalarFactory;
import org.opentripplanner.ext.transmodelapi.model.scalars.TimeScalarFactory;
import org.opentripplanner.routing.graphfinder.GraphFinder;
import org.opentripplanner.routing.vehicle_parking.VehicleParkingService;
import org.opentripplanner.service.vehiclerental.VehicleRentalService;
import org.opentripplanner.transit.service.TransitService;

/**
 * Provide some of the commonly used "chain" of methods. Like all ids should be created the same
 * wayThis
 */
public class GqlUtil {

  public final GraphQLScalarType dateTimeScalar;
  public final GraphQLScalarType dateScalar;
  public final GraphQLScalarType doubleFunctionScalar;
  public final GraphQLScalarType localTimeScalar;
  public final GraphQLObjectType timeScalar;
  public final GraphQLScalarType durationScalar;
  public final GraphQLDirective timingData;

  /** private to prevent util class from instantiation */
  public GqlUtil(ZoneId timeZone) {
    this.dateTimeScalar =
      DateTimeScalarFactory.createMillisecondsSinceEpochAsDateTimeStringScalar(timeZone);
    this.dateScalar = DateScalarFactory.createDateScalar();
    this.doubleFunctionScalar = DoubleFunctionScalarFactory.createDoubleFunctionScalar();
    this.localTimeScalar = LocalTimeScalarFactory.createLocalTimeScalar();
    this.timeScalar = TimeScalarFactory.createSecondsSinceMidnightAsTimeObject();
    this.durationScalar = DurationScalarFactory.createDurationScalar();
    this.timingData =
      GraphQLDirective
        .newDirective()
        .name("timingData")
        .description("Add timing data to prometheus, if Actuator API is enabled")
        .validLocation(DirectiveLocation.FIELD_DEFINITION)
        .build();
  }

  public static TransitService getTransitService(DataFetchingEnvironment environment) {
    return ((TransmodelRequestContext) environment.getContext()).getTransitService();
  }

  public static VehicleRentalService getVehicleRentalService(DataFetchingEnvironment environment) {
    return ((TransmodelRequestContext) environment.getContext()).getServerContext()
      .vehicleRentalService();
  }

  public static VehicleParkingService getVehicleParkingService(
    DataFetchingEnvironment environment
  ) {
    return ((TransmodelRequestContext) environment.getContext()).getServerContext()
      .graph()
      .getVehicleParkingService();
  }

  public static GraphFinder getGraphFinder(DataFetchingEnvironment environment) {
    return ((TransmodelRequestContext) environment.getContext()).getServerContext().graphFinder();
  }

  public static GraphQLFieldDefinition newTransitIdField() {
    return GraphQLFieldDefinition
      .newFieldDefinition()
      .name("id")
      .type(new GraphQLNonNull(Scalars.GraphQLID))
      .dataFetcher(env -> TransitIdMapper.mapEntityIDToApi(env.getSource()))
      .build();
  }

  public static GraphQLInputObjectField newIdListInputField(String name, String description) {
    return GraphQLInputObjectField
      .newInputObjectField()
      .name(name)
      .description(description)
      .type(new GraphQLList(Scalars.GraphQLID))
      .defaultValue(List.of())
      .build();
  }

  public static boolean hasArgument(DataFetchingEnvironment environment, String name) {
    return environment.containsArgument(name) && environment.getArgument(name) != null;
  }

  public static <T> List<T> listOfNullSafe(T element) {
    return element == null ? List.of() : List.of(element);
  }
}
