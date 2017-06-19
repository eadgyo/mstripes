from tkinter import filedialog
from constants import Constant
import os


def load_shp_file():
    name = filedialog.askopenfilename(initialdir="./", title=Constant.SELECT_SHAPE_TITLE,filetypes=(("shape  "
                                                                                                     "files",
                                                                                            "*.shx"),                                                                 ("all files","*.*")))
    file, _ = os.path.splitext(name)
    return file
