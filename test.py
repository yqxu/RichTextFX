from ast import *
import inspect
# This is a sample Python script.
class Foo:
    name = "foo"
    def __init__(self):
        self.pub = "c"
    def showName(self):
        name2 = self.name
        return self.pub + name2
# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.
a =1
foo = Foo();
b=2
c=3
str = inspect.getmembers(foo)
# See PyCharm help at https://www.jetbrains.com/help/pycharm/
script = '''

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press ⌘F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')
print("aaa test")
'''

astwitherror = '''
def print_hi(name):
    p
'''


ast = parse(script)

print(unparse(ast))

