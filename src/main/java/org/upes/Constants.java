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

    String NAME_OK_DIALOG = "OK";

    String TITLE_NOT_VALID_SHP = "Error ShapeFile";
    String MESSAGE_NOT_VALID_SHP = "Not a valid Shape File";

    String TITLE_NOT_VALID_TABLE = "Error DBX";
    String MESSAGE_NOT_VALID_TABLE = "Not valid DBX file";

    Dimension MIN_LAYERS_DIMENSION_SIZE = new Dimension(400, 300);

    Dimension MIN_MAP_DIMENSION_SIZE = new Dimension(400, 300);
}
