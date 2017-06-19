import tkinter as Tk
from tkinter import filedialog

from mpl_toolkits.basemap import Basemap
from constants import *
from tktable import *

from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

class App:

    def __init__(self, master):
        #variables to store the window dimentions

        master.geometry('%dx%d+%d+%d' % (master.winfo_screenwidth(),master.winfo_screenheight(),0,0))

        self.mwidth =master.winfo_screenwidth()
        self.mheight =master.winfo_screenheight()
        master.minsize(self.mwidth, self.mheight)
        # Create a container for the frame
        frame = Tk.Frame(master,width=self.mwidth,height=self.mheight)
        frame.pack_propagate(0)
        frame.pack()

        # Create the left part container
        leftwidth= self.mwidth / 5
        leftheight= self.mheight

        left_part = Tk.Frame(frame, width=leftwidth, height=self.mheight)
        left_part.pack_propagate(0)
        left_part.pack(side=Tk.LEFT)

        # Dividing left frame in three parts
        topframe = Tk.Frame(left_part, width=leftwidth, height=leftheight/5)
        topframe.pack_propagate(0)
        topframe.pack()
            # Load button
        self.loadButton = Tk.Button(topframe, text=Constant.LOAD_TEXT_BUTTON, command=self.loadShapeFile)
        self.loadButton.place(relx=.5, rely=.5,anchor=Tk.CENTER)

        middleframe = Tk.Frame(left_part, width=leftwidth, height=leftheight / 5)
        middleframe.pack_propagate(0)
        middleframe.pack()

        bottomframe = Tk.Frame(left_part, width=leftwidth, height=3 * leftheight / 5)
        bottomframe.pack_propagate(0)
        bottomframe.pack(padx=10)
            # Table
        self.table = Table(bottomframe, rows=5, cols=5)
        self.table.pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1)

        # Create the center part
        centerwidth = 3 * self.mwidth / 5
        centerheight = self.mheight
        center_part = Tk.Frame(frame,width=centerwidth,height=centerheight)
        center_part.pack_propagate(0)
        center_part.pack(side=Tk.LEFT)

        self.fig = Figure()
        self.ax1 = self.fig.add_subplot(111)

        self.map = Basemap(llcrnrlon=-0.5,llcrnrlat=39.8,urcrnrlon=4.,urcrnrlat=43.,
                           resolution='i', projection='tmerc', lat_0 = 39.5, lon_0 = 1, ax=self.ax1)

        self.canvas = FigureCanvasTkAgg(self.fig, master=center_part)
        self.canvas.show()
        self.canvas.get_tk_widget().pack(expand=1)

        # Create the right part
        rightwidth= self.mwidth / 5
        rightheight= self.mheight

        right_part = Tk.Frame(frame,width=rightwidth,height=rightheight)
        right_part.pack_propagate(0)
        right_part.pack(side=Tk.LEFT)

        right_topframe=Tk.Frame(right_part,width=rightwidth,height=rightheight/5)
        right_topframe.pack_propagate(0)
        right_topframe.pack()

        self.addButton = Tk.Button(right_topframe, text=Constant.ADD_TEXT_BUTTON)
        self.addButton.place(relx=.5, rely=.5, anchor="center")

    def loadShapeFile(self):
        self.filename = filedialog.askopenfilename(filetypes=(("Shape Files","*.shp"),))


if __name__ == '__main__':
    root = Tk.Tk()
    app = App(root)
    root.mainloop()