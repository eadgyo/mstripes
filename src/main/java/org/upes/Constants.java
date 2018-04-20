package org.upes;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eadgyo on 27/06/17.
 */
public interface Constants
{
    int TIME_LOADING = 0;

    String MAIN_WINDOW_TILE = "mstripes";
    String ADD_LAYER_DIALOG_TITLE = "Add layer";

    String NAME_LOAD_MAP = "Load";
    String DESC_LOAD_MAP = "Load the map";

    String NAME_REMOVE_MAP = "Remove Layer";
    String DESC_REMOVE_MAP = "Remove Layer from calculation";

    String NAME_DEL_MAP="Customize";
    String DESC_DEL_MAP="Customize layers from map";

    String NAME_ASSIGN_SCORE = "Assign Score";
    String DESC_ASSIGN_SCORE = "Assign cost factor to each type";

    String NAME_PATH = "Path";
    String DESC_PATH = "Create path for patrols";

    String NAME_CALC_INTERSECT="Calculate";
    String DESC_CALC_INTERSECT="Calculate the area of occupancy of each layer";
    String NAME_OK_DIALOG = "OK";

    String TITLE_NOT_VALID_SHP = "Error ShapeFile";

    Dimension MIN_LAYERS_DIMENSION_SIZE = new Dimension(400, 300);

    Dimension MIN_MAP_DIMENSION_SIZE = new Dimension(400, 300);

    String OPTIONS_QUESTION="The added layer is:";
    String StrNEUTRAL ="Neutral";
    String StrDEFECTIVE="Defective";
    String StrSUPPORTIVE="Supportive";
    String StrNONE="None";

    String CBOXHEADING = "Select Layer Type: ";

    // Image locations
    String IMAGE_UPES_PATH = "upes.png";
    String IMAGE_WILDLIFE_PATH = "wiilogo.gif";
    int WILDLIFE_PADDING = 80;
    int UPES_PADDING = 40;

    // are indicies for style factory
    int BEAT =0;
    int BEAT_I1=1;
    int BOUNDARY=2;
    int DIVISION=3;
    int FOREST_ROAD=0;
    int GRID=4;
    int PATROLLING_CAMP=0;
    int RANGE=5;
    int STATE=6;
    int WIRELESS_STATION=1;

    String ROAD_NAME_FILE = "forst_road_core";
    String PATROL_CHOWKIS = "Patrolling_camp";
    String WIRELESS_CHOWKI="Wireless_station";
//    String BEAT_NAME = "BEAT";
    String BEAT_FEATURE_NAME = "BEAT_N";
    String ATTRIBUTE_STATUS = "STATUS";
    double FACTOR_SCORE = 1000;

    int SUPPORTIVE=3;
    int DEFECTIVE=2;
    int COOPERATIVE=1;

    int SERVER_PORT = 8080;
    int NUMBER_RANKS = 5;


    String DESCRIPTION_ASK_DIALOG = "Enter coordinates value, or left click on map for starting grid and right click for ending grid.";
    String ASK_DIALOG_TITLE = "Path Creation";

    String DISTANCE_LABEL = "Distance";
    String DEFAULT_VALUE_DISTANCE_PATH = "9000";

    String START_LONGITUDE_LABEL = "Start Point Longitude";
    String START_LATITUDE_LABEL = "Start Point Latitude";

    String END_LONGITUDE_LABEL = "End Point Longitude";
    String END_LATITUDE_LABEL = "End Point Latitude";

//    String GRID_NAME="grd1ha";

    // List of Layer Types
    String TYPES[]={"Water Bodies","Electric Poles","Dense Forest Cover","Grasslands","Poaching","Roads","Patrol Chowkis","Neutral Element","Grid","Beats"};

    String JSONPATH = "shplist.json";
    String SQLPATH = "grid_values.db";

    int NO_CHANGE = 0;
    int NOT_REGISTERED = 1;
    int SCORE_CHANGED = 2;
    int ERROR = -1;

    String Distances[] = {"2-5 Km","6-10 Km","11-15 Km","16-20 Km"};
    Map<String,Integer> minDistance = Collections.unmodifiableMap(new HashMap<String,Integer>()
                                                                  {
                                                                      {
                                                                          put(Distances[0], 2);
                                                                          put(Distances[1],6);
                                                                          put(Distances[2],11);
                                                                          put(Distances[3],16);
                                                                      }
                                                                  }

    );

    Map<String,Integer> maxDistance = Collections.unmodifiableMap(new HashMap<String,Integer>()
                                                                  {
                                                                      {
                                                                          put(Distances[0], 5);
                                                                          put(Distances[1],10);
                                                                          put(Distances[2],15);
                                                                          put(Distances[3],20);
                                                                      }
                                                                  }

    );

}
