package org.upes;

import java.awt.*;

/**
 * Created by eadgyo on 27/06/17.
 */
public interface Constants
{
    String MAIN_WINDOW_TILE = "mstripes";
    String ADD_LAYER_DIALOG_TITLE = "Add layer";

    String NAME_LOAD_MAP = "Load";
    String DESC_LOAD_MAP = "Load the map";

    String NAME_ADD_MAP = "Add";
    String DESC_ADD_MAP = "Add details to the map";

    String NAME_DEL_MAP="Delete";
    String DESC_DEL_MAP="Delete layers from map";

    String NAME_OK_DIALOG = "OK";

    String TITLE_NOT_VALID_SHP = "Error ShapeFile";
    String MESSAGE_NOT_VALID_SHP = "Not a valid Shape File";

    String TITLE_NOT_VALID_TABLE = "Error DBX";
    String MESSAGE_NOT_VALID_TABLE = "Not valid DBX file";

    Dimension MIN_LAYERS_DIMENSION_SIZE = new Dimension(400, 300);

    Dimension MIN_MAP_DIMENSION_SIZE = new Dimension(400, 300);

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

}
