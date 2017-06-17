from tkinter import *
from tktable import *
from constants import *
from mpl_toolkits.basemap import Basemap

from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg


class App:
    def __init__(self, master):
        frame = Frame(master)
        frame.pack()

        # Create the left part
        self.loadButton = Button(frame, text=Constant.LOAD_TEXT_BUTTON)
        self.loadButton.grid(row=0, column=0)

        self.table = Table(frame, rows=5, cols=5)
        self.table.grid(row=2, column=0)


        # Create the center part
        self.map = Basemap(llcrnrlon=-0.5,llcrnrlat=39.8,urcrnrlon=4.,urcrnrlat=43.,
                           resolution='i', projection='tmerc', lat_0 = 39.5, lon_0 = 1, ax=self.ax1)
        self.map.grid(row=0, column=1)

        self.fig = Figure()
        self.ax1 = self.fig.add_subplot(111)
        self.canvas = FigureCanvasTkAgg(self.fig, master=master)
        self.canvas.show()

        # Create the right part
        self.addButton = Button(frame, text=Constant.ADD_TEXT_BUTTON)
        self.addButton.grid(row=0, column=2)


if __name__ == '__main__':
    root = Tk()
    app = App(root)
    root.mainloop()