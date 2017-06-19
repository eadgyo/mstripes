import tools
import matplotlib.pyplot as plt
from tkinter import messagebox
from mpl_toolkits.basemap import Basemap
from mpl_toolkits.basemap.data import *


class Controller:
    def __init__(self, view, model):
        self.view = view
        self.model = model

        # Add command
        self.view.loadButton.configure(command=self.load_action)
        self.view.addButton.configure(command=self.add_action)

    def load_action(self):
        print("Load")

        path_shp = tools.load_shp_file()
        self.model.set_last_loaded_shape(path_shp)

    def add_action(self):
        print("Add " + self.model.lastLoadedShapePath)
        if not self.model.lastLoadedShapePath:
            messagebox.showerror("File missing", "No loaded file")
            return


        # Add shape information to the map
        shp = self.view.map.readshapefile(self.model.lastLoadedShapePath, self.model.lastLoadedShapePath,
                                          ax=self.view.ax1)
        self.view.canvas.show()

        # Add shape table information to the table
        res = getattr(self.view.map, self.model.lastLoadedShapePath)
        res_info = getattr(self.view.map, self.model.lastLoadedShapePath + "_info")
        pol_info = zip(res_info, res)

        self.model.add_shape(pol_info)

        # Add keys if not in table
        for info, shape in pol_info:
            for key in info.keys():
                if not self.view.table.index(key):
                    self.view.table.insert_cols(key)

        # Add values if not in table
        for info, shape in pol_info:
            for values in info.items():
                r = self.view.table.index(values[0]).split(',')[0] # get row number
                