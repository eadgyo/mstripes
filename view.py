import tkinter as Tk

from constants import *
from tktable import *
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from mpl_toolkits.basemap import Basemap


class View:
    def __init__(self, master):
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
        self.create_left_part()
        self.create_map()
        self.create_right_part()

    def create_left_part(self):
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
        hscrollbar = Tk.Scrollbar(bottomframe, orient=Tk.HORIZONTAL)
        hscrollbar.pack(fill=Tk.X, side=Tk.TOP, expand=Tk.FALSE)
        vscrollbar=Tk.Scrollbar(bottomframe,orient=Tk.VERTICAL)
        vscrollbar.pack(fill=Tk.Y, side=Tk.RIGHT, expand=Tk.FALSE)
        self.table = Table(bottomframe, rows=35, cols=1,foreground="black",variable=Constant.TABLE,xscrollcommand=hscrollbar.set,yscrollcommand=vscrollbar.set)
        self.table.tag_configure('active', foreground='black')
        self.table.pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1)
        hscrollbar.config(command=self.__xscrollHandler)
        vscrollbar.config(command=self.__yscrollHandler)



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

    def __xscrollHandler(self, *L):
        op, howMany = L[0], L[1]

        if op == 'scroll':
            units = L[2]
            self.table.xview_scroll(howMany, units)
        elif op == 'moveto':
            self.table.xview_moveto(howMany)

    def __yscrollHandler(self, *L):
        op, howMany = L[0], L[1]

        if op == 'scroll':
            units = L[2]
            self.table.yview_scroll(howMany, units)
        elif op == 'moveto':
            self.table.yview_moveto(howMany)