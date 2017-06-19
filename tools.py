from tkinter import filedialog
from constants import Constant
import os


def load_shp_file():
    name = filedialog.askopenfilename(initialdir="./", title=Constant.SELECT_SHAPE_TITLE,filetypes=(("shape  "
                                                                                                     "files",
                                                                                            "*.shx"),                                                                 ("all files","*.*")))
    file, _ = os.path.splitext(name)
    return file


def get_boundaries(shp):
    x_min = shp[2][0]
    y_min = shp[2][1]
    x_max = shp[3][0]
    y_max = shp[3][1]
    #print("boundaries :" + str(x_min) + " " + str(x_max) + "    " + str(y_min) + " " + str(y_max))
    return (x_min, x_max, y_min, y_max)
