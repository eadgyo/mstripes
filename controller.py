import tools
import matplotlib.pyplot as plt
from tkinter import messagebox
from mpl_toolkits.basemap import Basemap
from mpl_toolkits.basemap.data import *


class Controller:
    def __init__(self, view, model):
        self.view = view
        self.model = model
        self.table = self.view.table

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

        self.add_shape_map()
        self.add_shape_table()

    def add_shape_map(self):
        # Add shape to the map
        self.view.map.readshapefile(self.model.lastLoadedShapePath, self.model.lastLoadedShapePath,
                                          ax=self.view.ax1)
        self.view.canvas.show()
        self.model.add_shape_path()

    def add_shape_table(self):
        # Add shape info in the table
        shape_info = self._get_shape_info()
        self._update_keys_table(shape_info)
        shape_info = self._get_shape_info()
        self._add_values_table(shape_info)

    def _update_keys_table(self, shape_info):
        # Add key if it does not already exist in table
        for info, shape in shape_info:
            for key in info.keys():
                if not self._key_col_exists(key):
                    # if array not empty
                    if len(self.model.array.get()) > 1:
                        self.table.insert_cols("end", 1)
                    c = self._get_last_col_index()
                    self._set_column_title(c, key)

    def _add_values_table(self, shape_info):
        # Add values from the shape
        for info, shape in shape_info:
            self.table.insert_rows("end", 1)
            row_id = self._get_last_row_index()
            for value in info.items():
                col_id = self._get_col_index(value[0])
                self._set_cell(row_id, col_id, value[1])

    def _get_last_col_index(self):
        return self.table.index('end').split(',')[1]  # Get column number

    def _get_last_row_index(self):
        return self.table.index('end').split(',')[0]  # Get row number

    def _get_number_col(self):
        return self._get_last_col_index() + 1

    def _get_number_row(self):
        return self._get_last_row_index() + 1

    def _get_col_index(self, col_name):
        for index, name in self.model.array.get().items():
            if col_name == name:
                return int(index.split(',')[1])
        return -1

    def _get_shape_info(self):
        res = getattr(self.view.map, self.model.lastLoadedShapePath)
        res_info = getattr(self.view.map, self.model.lastLoadedShapePath + "_info")
        return zip(res_info, res)

    def _set_cell(self, row, col, value):
        self.table.set("row", tools.get_index(row, col), *(value,))

    def _set_column_title(self, col, name):
        self._set_cell(-1, col, name)

    def _set_row_title(self, row, name):
        self._set_cell(row, -1, name)

    def _key_col_exists(self, k):
        return self._get_col_index(k) != -1
