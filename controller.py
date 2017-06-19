from tools import *
import matplotlib.pyplot as plt
from tkinter import messagebox
from mpl_toolkits.basemap import Basemap


class Controller:
    def __init__(self, view, model):
        self.view = view
        self.model = model

        # Add command
        self.view.loadButton.configure(command=self.load_action)
        self.view.addButton.configure(command=self.add_action)

    def load_action(self):
        print("Load")

        path_shp = load_shp_file()
        self.model.set_last_loaded_shape(path_shp)

    def add_action(self):
        print("Add " + self.model.lastLoadedShapePath)
        if not self.model.lastLoadedShapePath:
            messagebox.showerror("File missing", "No loaded file")
            return

        self.model.add_shape()
        # Add shape information to the map

        self.view.map.drawcoastlines()
        self.view.canvas.show()
        #self.view.map.readshapefile(self.model.lastLoadedShapePath, self.model.lastLoadedShapePath)


        #map.readshapefile(self.model.lastLoadedShapePath, self.model.lastLoadedShapePath)

        #self.view.canvas.show()

    # Add shape table information to the table
        pass



