import tkinter as Tk
from tkinter import filedialog

from mpl_toolkits.basemap import Basemap
import matplotlib.pyplot as plt
from model import Model
from view import View
from controller import Controller
from tktable import ArrayVar

class App:
    def __init__(self):
        self.root = Tk.Tk()
        array = ArrayVar(self.root)

        self.model = Model(array)
        self.view = View(self.root, array)
        self.controller = Controller(self.view, self.model)
        self.debug()

        self.root.mainloop()

    def debug(self):
        self.model.lastLoadedShapePath = "./try"

    def test(self):
        plt.figure(0)
        map = Basemap()
        map.readshapefile("./try", 'try')
        plt.show()



if __name__ == '__main__':
    app = App()