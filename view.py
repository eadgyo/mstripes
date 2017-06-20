import tkinter as Tk
from tkinter import Tcl
from constants import *
from tktable import *
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from mpl_toolkits.basemap import Basemap


class View:
    def __init__(self, master, array):
        #variables to store the window dimentions

        master.geometry('%dx%d+%d+%d' % (master.winfo_screenwidth(),master.winfo_screenheight(),0,0))

        self.mwidth =master.winfo_screenwidth()
        self.mheight =master.winfo_screenheight()
        master.minsize(self.mwidth, self.mheight)

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
        self.create_left_part(array)
        self.create_map()

    def create_left_part(self, array):
        leftwidth= self.mwidth / 5
        leftheight= self.mheight

        left_part = Tk.Frame(self.frame, width=leftwidth, height=self.mheight)
        left_part.pack_propagate(0)
        left_part.pack(side=Tk.LEFT)

        # Dividing left frame in three parts
        topframe = Tk.Frame(left_part, width=leftwidth, height=leftheight/5)
        topframe.pack_propagate(0)
        topframe.pack()
        # Load button
        self.loadButton = Tk.Button(topframe, text=Constant.LOAD_TEXT_BUTTON)
        self.loadButton.place(relx=.5, rely=.5,anchor=Tk.CENTER)

        middleframe = Tk.Frame(left_part, width=leftwidth, height=leftheight / 5)
        middleframe.pack_propagate(0)
        middleframe.pack()

        bottomframe = Tk.Frame(left_part, width=leftwidth, height=3 * leftheight / 5)
        bottomframe.pack_propagate(0)
        bottomframe.pack(padx=10)
        # Table

        Table(variable={})
        self.table = Table(bottomframe, rows=0, cols=0, roworigin=-1, colorigin=0, titlerows=1, variable=array)
        self.table.pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1)

    def create_map(self):
        centerwidth = 3 * self.mwidth / 5
        centerheight = self.mheight
        center_part = Tk.Frame(self.frame,width=centerwidth,height=centerheight)
        center_part.pack_propagate(0)
        center_part.pack(side=Tk.LEFT)

        fig = Figure()
        self.ax1 = fig.add_subplot(111)
        self.map = map = Basemap(ax=self.ax1)

        self.canvas = FigureCanvasTkAgg(fig, master=center_part)
        self.canvas.show()
        self.canvas.get_tk_widget().pack(expand=1)

    def create_right_part(self):
        rightwidth= self.mwidth / 5
        rightheight= self.mheight

        right_part = Tk.Frame(self.frame,width=rightwidth,height=rightheight)
        right_part.pack_propagate(0)
        right_part.pack(side=Tk.LEFT)

        right_topframe=Tk.Frame(right_part,width=rightwidth,height=rightheight/5)
        right_topframe.pack_propagate(0)
        right_topframe.pack()

        right_part = Tk.Frame(self.frame)
        right_part.pack(side=Tk.LEFT)

        self.addButton = Tk.Button(right_topframe, text=Constant.ADD_TEXT_BUTTON)
        self.addButton.place(relx=.5, rely=.5, anchor="center")
