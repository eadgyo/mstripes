from tkinter import filedialog
from constants import Constant
import os


def load_shp_file():
    file = filedialog.askopenfilename(initialdir="./", title=Constant.SELECT_SHAPE_TITLE,filetypes=(("shape  "
                                                                                                     "files",
                                                                                            "*.shp"), ))
    if isinstance(file, tuple):
        if len(file) != 0:
            file, _ = os.path.splitext(file[0])
            return file[0]
        return ""
    elif file != "":
        file, _ = os.path.splitext(file)
    return file




def get_boundaries(shp):
    x_min = shp[2][0]
    y_min = shp[2][1]
    x_max = shp[3][0]
    y_max = shp[3][1]
    #print("boundaries :" + str(x_min) + " " + str(x_max) + "    " + str(y_min) + " " + str(y_max))
    return (x_min, x_max, y_min, y_max)
