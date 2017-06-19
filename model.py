class Model:
    def __init__(self):
        # Store the path of the last loaded shape file
        self.lastLoadedShapePath = ""

        # Store the table containing all information on shape
        self.table = []

        # Store all added shape to the map
        self.addedShapes = {}

    def set_last_loaded_shape(self, last_loaded_shape_path):
        self.lastLoadedShapePath = last_loaded_shape_path

        if self.lastLoadedShapePath == "":
            self.lastLoadedShape = None
        else:
            pass
            # Load shape files
            #self.lastLoadedShape

    # Add the last loaded shape file to the project
    def add_shape(self):
        self.addedShapes[self.lastLoadedShapePath] = 1




