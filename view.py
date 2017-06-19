import tkinter as Tk
from constants import *
from tktable import *
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from mpl_toolkits.basemap import Basemap


class View:
    def __init__(self, master):
        # Create a container for the frame
        self.frame = Tk.Frame(master)
        self.frame.pack()

        # Init used object
        self.loadButton = None
        self.table = None
        self.map = None
        self.canvas = None
        self.addButton = None

        # Create part
        self.create_right_part()
        self.create_map()

        self.create_left_part()

    def create_left_part(self):
        left_part = Tk.Frame(self.frame)
        left_part.pack(side=Tk.LEFT)

        self.loadButton = Tk.Button(left_part, text=Constant.LOAD_TEXT_BUTTON)
        self.loadButton.pack(side=Tk.TOP)

        self.table = Table(left_part, rows=5, cols=5)
        self.table.pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1)

    def create_map(self):
        center_part = Tk.Frame(self.frame)
        center_part.pack(side=Tk.LEFT)

        fig = Figure()
        ax1 = fig.add_subplot(111)
        self.map = map = Basemap(llcrnrlon=-0.5,llcrnrlat=39.8,urcrnrlon=4.,urcrnrlat=43.,
                                 resolution='i', projection='tmerc', lat_0=39.5, lon_0=1, ax=ax1)

        self.canvas = FigureCanvasTkAgg(fig, master=center_part)
        self.canvas.show()
        self.canvas.get_tk_widget().pack(expand=1)

    def create_right_part(self):
        right_part = Tk.Frame(self.frame)
        right_part.pack(side=Tk.LEFT)

        self.addButton = Tk.Button(right_part, text=Constant.ADD_TEXT_BUTTON)
        self.addButton.pack(side=Tk.TOP)