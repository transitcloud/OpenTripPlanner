package org.opentripplanner.api.ws;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.opentripplanner.api.model.error.ParameterException;
import org.opentripplanner.routing.core.OptimizeType;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.core.TraverseOptions;
import org.springframework.context.annotation.Scope;

/**
 * This class defines all the JAX-RS query parameters for a path search as fields, allowing them to be inherited
 * by other REST resource classes (the trip planner and the Analyst WMS or tile resource). They will be properly
 * included in API docs generated by Enunciate.
 * 
 * @author abyrd
 */
@Scope("request")
public abstract class SearchResource {

    /* not sure if it's really worth using symbolic constants here but that's how it was done in RequestInf */
    public static final String ROUTER_ID = "routerId";
    public static final String FROM = "fromPlace";
    public static final String TO = "toPlace";
    public static final String INTERMEDIATE_PLACES = "intermediatePlaces";
    public static final String DATE = "date";
    public static final String TIME = "time";

    public static final String MAX_WALK_DISTANCE = "maxWalkDistance";
    public static final String OPTIMIZE = "optimize";
    public static final String MODE = "mode";
    public static final String NUMBER_ITINERARIES = "numItineraries";
    public static final String SHOW_INTERMEDIATE_STOPS = "showIntermediateStops";

    public static final String PREFERRED_ROUTES = "preferredRoutes";
    public static final String UNPREFERRED_ROUTES = "unpreferredRoutes";
    public static final String BANNED_ROUTES = "bannedRoutes";
    
    public static final String ARRIVE_BY = "arriveBy";
    public static final String WALK_SPEED = "walkSpeed";
    public static final String WHEELCHAIR = "wheelchair";
    public static final String MIN_TRANSFER_TIME = "minTransferTime";
    public static final String TRIANGLE_TIME_FACTOR = "triangleTimeFactor";
    public static final String TRIANGLE_SAFETY_FACTOR = "triangleSafetyFactor";
    public static final String TRIANGLE_SLOPE_FACTOR = "triangleSlopeFactor";
    public static final String TRANSFER_PENALTY = "transferPenalty";
    public static final String LANGUAGE = "lang";
    public static final String MAX_TRANSFERS = "maxTransfers";
    public static final String INTERMEDIATE_PLACES_ORDERED = "intermediatePlacesOrdered";

    /** The start location -- either latitude, longitude pair in degrees or a Vertex
     *  label. For example, <code>40.714476,-74.005966</code> or
     *  <code>mtanyctsubway_A27_S</code>.  */
    @QueryParam(FROM) String fromPlace;

    /** The end location (see fromPlace for format). */
    @QueryParam(TO) String toPlace;

    /** An unordered list of intermediate locations to be visited (see the fromPlace for format). */
    @QueryParam(INTERMEDIATE_PLACES) List<String> intermediatePlaces;
    
    @DefaultValue("false") @QueryParam(INTERMEDIATE_PLACES_ORDERED) Boolean intermediatePlacesOrdered;
    
    /** The date that the trip should depart (or arrive, for requests where arriveBy is true). */
    @QueryParam(DATE) String date;
    
    /** The time that the trip should depart (or arrive, for requests where arriveBy is true). */
    @QueryParam(TIME) String time;
    
    /** Router ID used when in multiple graph mode. Unused in singleton graph mode. */
    @DefaultValue("") @QueryParam(ROUTER_ID) String routerId;
    
    /** Whether the trip should depart or arrive at the specified date and time. */
    @DefaultValue("false") @QueryParam(ARRIVE_BY) Boolean arriveBy;
    
    /** Whether the trip must be wheelchair accessible. */
    @DefaultValue("false") @QueryParam(WHEELCHAIR) Boolean wheelchair;

    /** The maximum distance (in meters) the user is willing to walk. Defaults to approximately 1/2 mile. */
    @DefaultValue("800") @QueryParam(MAX_WALK_DISTANCE) Double maxWalkDistance;

    /** The user's walking speed in meters/second. Defaults to approximately 3 MPH. */
    @QueryParam(WALK_SPEED) Double walkSpeed;

    /** For bike triangle routing, how much safety matters (range 0-1). */
    @QueryParam(TRIANGLE_SAFETY_FACTOR) Double triangleSafetyFactor;
    
    /** For bike triangle routing, how much slope matters (range 0-1). */
    @QueryParam(TRIANGLE_SLOPE_FACTOR) Double triangleSlopeFactor;
    
    /** For bike triangle routing, how much time matters (range 0-1). */            
    @QueryParam(TRIANGLE_TIME_FACTOR) Double triangleTimeFactor;

    /** The set of characteristics that the user wants to optimize for. @See OptimizeType */
    @DefaultValue("QUICK") @QueryParam(OPTIMIZE) OptimizeType optimize;
    
    /** The set of modes that a user is willing to use. */
    @DefaultValue("TRANSIT,WALK") @QueryParam(MODE) TraverseModeSet modes;

    /** The minimum time, in seconds, between successive trips on different vehicles.
     *  This is designed to allow for imperfect schedule adherence.  This is a minimum;
     *  transfers over longer distances might use a longer time. */
    @DefaultValue("240") @QueryParam(MIN_TRANSFER_TIME) Integer minTransferTime;

    /** The maximum number of possible itineraries to return. */
    @DefaultValue("3") @QueryParam(NUMBER_ITINERARIES) Integer numItineraries;

    /** The list of preferred routes.  The format is agency_route, so TriMet_100. */
    @DefaultValue("") @QueryParam(PREFERRED_ROUTES) String preferredRoutes;
    
    /** The list of unpreferred routes.  The format is agency_route, so TriMet_100. */
    @DefaultValue("") @QueryParam(UNPREFERRED_ROUTES) String unpreferredRoutes;

    /** Whether intermediate stops -- those that the itinerary passes in a vehicle, but 
     *  does not board or alight at -- should be returned in the response.  For example,
     *  on a Q train trip from Prospect Park to DeKalb Avenue, whether 7th Avenue and
     *  Atlantic Avenue should be included. */
    @DefaultValue("false") @QueryParam(SHOW_INTERMEDIATE_STOPS) Boolean showIntermediateStops;

    /** The list of banned routes.  The format is agency_route, so TriMet_100. */
    @DefaultValue("") @QueryParam(BANNED_ROUTES) String bannedRoutes;

    /** An additional penalty added to boardings after the first.  The value is in OTP's
     *  internal weight units, which are roughly equivalent to seconds.  Set this to a high
     *  value to discourage transfers.  Of course, transfers that save significant
     *  time or walking will still be taken.*/
    @DefaultValue("0") @QueryParam(TRANSFER_PENALTY) Integer transferPenalty;
    
    /** The maximum number of transfers (that is, one plus the maximum number of boardings)
     *  that a trip will be allowed.  Larger values will slow performance, but could give
     *  better routes.  This is limited on the server side by the MAX_TRANSFERS value in
     *  org.opentripplanner.api.ws.Planner. */
    @DefaultValue("2") @QueryParam(MAX_TRANSFERS) Integer maxTransfers;

    /** Inject the servlet request so we have access to the query parameter map */
    @Context HttpServletRequest httpServletRequest;

    /** 
     * Range/sanity check the query parameter fields and build a Request object from them. 
     * @throws ParameterException when there is a problem interpreting a query parameter
     */
    protected TraverseOptions buildRequestFromQueryParamFields() throws ParameterException {
        TraverseOptions request = new TraverseOptions();
        request.setRouterId(routerId);
        request.setFrom(fromPlace);
        request.setTo(toPlace);
        request.setDateTime(date, time);
        request.setWheelchair(wheelchair);
        if (numItineraries != null) {
            request.setNumItineraries(numItineraries);
        }
        if (maxWalkDistance != null) {
            request.setMaxWalkDistance(maxWalkDistance);
        }
        if (walkSpeed != null) {
            request.setSpeed(walkSpeed);
        }
        if (triangleSafetyFactor != null || triangleSlopeFactor != null || triangleTimeFactor != null) {
            if (triangleSafetyFactor == null || triangleSlopeFactor == null || triangleTimeFactor == null) {
                throw new ParameterException(Message.UNDERSPECIFIED_TRIANGLE);
            }
            if (optimize == null) {
                optimize = OptimizeType.TRIANGLE;
            }
            if (optimize != OptimizeType.TRIANGLE) {
                throw new ParameterException(Message.TRIANGLE_OPTIMIZE_TYPE_NOT_SET);
            }
            if (Math.abs(triangleSafetyFactor + triangleSlopeFactor + triangleTimeFactor - 1) > Math.ulp(1) * 3) {
                throw new ParameterException(Message.TRIANGLE_NOT_AFFINE);
            }
            request.setTriangleSafetyFactor(triangleSafetyFactor);
            request.setTriangleSlopeFactor(triangleSlopeFactor);
            request.setTriangleTimeFactor(triangleTimeFactor);
        } else if (optimize == OptimizeType.TRIANGLE) {
            throw new ParameterException(Message.TRIANGLE_VALUES_NOT_SET);
        }
        if (arriveBy != null && arriveBy) {
            request.setArriveBy(true);
        }
        if (showIntermediateStops != null && showIntermediateStops) {
            request.setShowIntermediateStops(true);
        }
        if (intermediatePlaces != null && intermediatePlaces.size() > 0
                && !intermediatePlaces.get(0).equals("")) {
            request.setIntermediatePlaces(intermediatePlaces);
        }
        if (intermediatePlacesOrdered != null) {
            request.setIntermediatePlacesOrdered(intermediatePlacesOrdered);
        }
        if (preferredRoutes != null && !preferredRoutes.equals("")) {
            request.setPreferredRoutes(preferredRoutes);
        }
        if (unpreferredRoutes != null && !unpreferredRoutes.equals("")) {
            request.setUnpreferredRoutes(unpreferredRoutes);
        }
        if (bannedRoutes != null && !bannedRoutes.equals("")) {
            request.setBannedRoutes(bannedRoutes);
        }
        //replace deprecated optimization preference
        if (optimize == OptimizeType.TRANSFERS) {
            optimize = OptimizeType.QUICK;
            transferPenalty += 1800;
        }
        request.setTransferPenalty(transferPenalty);
        request.setOptimize(optimize);
        request.setModes(modes);
        request.setMinTransferTime(minTransferTime);

        if (maxTransfers != null) {
            request.setMaxTransfers(maxTransfers);
        }
        return request;
    }

}
