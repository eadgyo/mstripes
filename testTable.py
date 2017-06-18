from tkinter import *
import tktable

root = Tk()
root.title("Probabilidade e estatistica")

table = tktable.Table(root, rows=2, cols=10)
table.pack()

root.mainloop()
