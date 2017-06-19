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

    # Add the last loaded shape file to the project
    def add_shape(self, pol_info):
        self.addedShapes[self.lastLoadedShapePath] = 1

        # Read pol info
        for info, shape in pol_info:
            print(info)

    # Add one entry in the table
    def add_entry(self, info):
        pass





