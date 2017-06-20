from tktable import ArrayVar
import tools

class Model:
    def __init__(self, array):
        # Store the path of the last loaded shape file
        self.lastLoadedShapePath = ""

        # Keeps the array values of the table
        self.array = array

        # Store all added shape to the map
        self.addedShapes = {}

    def set_last_loaded_shape(self, last_loaded_shape_path):
        self.lastLoadedShapePath = last_loaded_shape_path

    def add_shape_path(self):
        # Add the last loaded shape file to the project
        self.addedShapes[self.lastLoadedShapePath] = 1









