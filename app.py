import tkinter as Tk
from mpl_toolkits.basemap import Basemap
from constants import *
from tktable import *

from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg


class App:
    def __init__(self, master):
        # Create a container for the frame
        frame = Tk.Frame(master)
        frame.pack()

        # Create the left part container
        left_part = Tk.Frame(frame)
        left_part.pack(side=Tk.LEFT)

        self.loadButton = Tk.Button(left_part, text=Constant.LOAD_TEXT_BUTTON)
        self.loadButton.pack(side=Tk.TOP)

        self.table = Table(left_part, rows=5, cols=5)
        self.table.pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1)

        # Create the center part
        center_part = Tk.Frame(frame)
        center_part.pack(side=Tk.LEFT)

        self.fig = Figure()
        self.ax1 = self.fig.add_subplot(111)

        self.map = Basemap(llcrnrlon=-0.5,llcrnrlat=39.8,urcrnrlon=4.,urcrnrlat=43.,
                           resolution='i', projection='tmerc', lat_0 = 39.5, lon_0 = 1, ax=self.ax1)

        self.canvas = FigureCanvasTkAgg(self.fig, master=center_part)
        self.canvas.show()
        self.canvas.get_tk_widget().pack(expand=1)

        # Create the right part
        right_part = Tk.Frame(frame)
        right_part.pack(side=Tk.LEFT)

        self.addButton = Tk.Button(right_part, text=Constant.ADD_TEXT_BUTTON)
        self.addButton.pack(side=Tk.TOP)


if __name__ == '__main__':
    root = Tk.Tk()
    app = App(root)
    root.mainloop()