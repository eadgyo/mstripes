from constants import *
from view import View
from controller import Controller
from model import Model
import tkinter as Tk
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from mpl_toolkits.basemap import Basemap
import matplotlib.pyplot as plt

class App:
    def __init__(self):
        self.root = Tk.Tk()

        self.model = Model()
        self.view = View(self.root)
        self.controller = Controller(self.view, self.model)

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