from constants import *
from view import View
from controller import Controller
from model import Model
import tkinter as Tk
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg


class App:
    def __init__(self):
        self.root = Tk.Tk()

        self.view = View(self.root)
        self.model = Model()
        self.debug()
        self.controller = Controller(self.view, self.model)

        self.root.mainloop()

    def debug(self):
        self.model.lastLoadedShapePath = "/home/ronan-j/Downloads/chowki"

if __name__ == '__main__':
    app = App()