package org.upes;

import java.awt.*;

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

    String NAME_ADD_MAP = "Add";
    String DESC_ADD_MAP = "Add details to the map";

    String NAME_DEL_MAP="Customize";
    String DESC_DEL_MAP="Customize layers from map";

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
    int SUPPORTIVE=3;
    int DEFECTIVE=2;
    int COOPERATIVE=1;

    int SERVER_PORT = 8080;
}
