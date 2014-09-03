# Jordan D. Nelson
# Nashville, Tennessee 
# This program is a script that parses a text document
# and creates an html file 

import sys 
import string
import re 
##Class Definition.  There is only one class to solve this problem. 
###################### 

class Command: 
    def __init__(self, a_string):
        self.img_name = "" 
        self.text = ""  
        self.title = "" 
        self.tag = ""
        self.category = ""
        self.units = list()
        self.set_category(a_string)
        self.set_units(a_string)
        a_string = self.get_title(a_string)
        a_string = self.get_img_name(a_string)
        self.get_text(a_string)  
        self.get_tag()
        self.external_images()
        
    def set_units(self, a_string):
        my_tag = "<units>"
        # units specification is optional
        if my_tag not in a_string:
            return
        start_index = a_string.find(my_tag) + len(my_tag)
        end_index = a_string.find('\n', start_index)
        units_string = a_string[start_index:end_index]
        # split on commas
        self.units = re.split(",", units_string) 
        self.units = map(str.strip, self.units)
        pass
        
    def set_category(self, a_string):
        my_tag = "<category>"
        # category specification is optional
        if my_tag not in a_string:
            return
        start_index = a_string.find(my_tag) + len(my_tag)
        end_index = a_string.find('\n', start_index)
        self.category = a_string[start_index:end_index]
        self.category = self.category.strip()
        pass

    def get_title(self, a_string): 
        an_index = a_string.find("<title>") + len("<title>")
        a_string = a_string[an_index:]
        an_index = a_string.find("\n")
        self.title = a_string[:an_index]
        return a_string

    def get_img_name(self, a_string):
        an_index = a_string.find("<image>") + len("<image>")  
        a_string = a_string[an_index:]
        an_index = a_string.find("\n")
        self.img_name = a_string[:an_index]
        return a_string

    def get_text(self, a_string):
        an_index = a_string.find("<text>") + len("<text>")
        self.text = rplc_newl_w_spac(a_string[an_index:].strip('\n\t'))

    def get_tag(self): 
        if not self.img_name: 
            return
        index1 = self.img_name.find(".")
        self.tag = self.img_name[:index1]

    def external_images(self):  
        if "<extern_img" not in self.text:
            return
        index1 = self.text.find("<extern_img")
        index2 = self.text.find(">", index1) + 1 
        index3 = self.text.find("=", index1) + 1
        index4 = self.text.find(".png", index1)
        tag = self.text[index3:index4] 
        tag = string.lstrip(tag, ' ') 
        crazy_string = "<a href=\"#"
        crazy_string += tag
        crazy_string += "\"><img src = \"../blockImages/"
        crazy_string += tag
        crazy_string += ".png\" class = \"intext\" /></a>"
        self.text = string.replace(self.text, self.text[index1:index2], crazy_string, 1) 
        self.external_images()

    def toString(self):
        result = "\n\n    <h3 class = \"speech\"><a id=\""
        result += self.tag
        result += '\">'
        result += self.title
        result += '</a></h3>\n    <p><img src = \"../blockImages/'
        result += self.img_name
        result += "\" />\n    <br />\n        "
        result += self.text
        result += '\n    </p>'
        return result

##Function Definitions
######################

def read_it(rf):
	text = ""
	for line in rf:
		text += line
	return text

def rplc_newl_w_spac(text):
    return re.sub('\n+', ' ', text) 


def split_text(text): 	
    result = []
    result = text.split("\\command")
    result.pop(0)  #get rid of first item in list which is an empty string.
    return result

def load_commands(a_list): 
    result = []
    for command_string in a_list:
        command = Command(command_string)
        result.append(command)
    result.sort(key = lambda obj: obj.title)    # list of Commands, by obj.title.  Alphabetical order
    return result 
        
def get_units_list(commands):
    result = list()
    for command in commands:
        current_units = command.units
        for unit in current_units:
            if unit not in result:
                result.append(unit)
    return result

def get_categories_list(commands):
    result = list()
    for command in commands:
        current_category = command.category
        if current_category != "" and current_category not in result:
            result.append(current_category) 
    result.sort()
    return result

def get_commands_string(commands):
    result = ""
    for command in commands:
        result += command.toString()
    return result

def get_commands_string_for_category(commands, a_category):
    result = ""
    for command in commands:
        if command.category == a_category:
            result += command.toString()
    return result

def get_commands_string_for_unit(commands, a_unit):
    result = ""
    for command in commands:
        if a_unit in command.units:
            result += command.toString()
    return result

def html_name(filter):
    return "manual_" + filter + ".html"

def get_links_to_filter_pages(units, categories, own_page):
    result = ""
    home_page = "Show All"
    if own_page == home_page:
        result += "<span><b>Show All</b></span>"
    else:
        result += "<a href = \"manual.html\">Show All</a>"
    result += "<br /><b>Categories:</b> "
    
    for category in categories:
        if category == own_page:
            result += "<b>" + category + "</b>"
        else:   
            result += "<a href = \"" + html_name(category) + "\">" + category + "</a>"
        result += " "
    result += "<br /><b>Special Units:</b> "
    for unit in units:
        if unit == own_page:
            result += "<b>" + unit + "</b>"
        else:
            result += "<a href = \"" + html_name(unit) + "\">" + unit + "</a>"
        result += " "
    result += "<br />"
    return result

def main():
    specified_file = sys.argv[1] 
    theFile = open(specified_file, "r")  
    text = theFile.read()  
    theFile.close()

    commands = load_commands(split_text(text))
    
    units = get_units_list(commands)             # list of units 
    print(units)
    categories = get_categories_list(commands)   # list of categories
    print(categories)
    links_to_filter_pages = get_links_to_filter_pages(units, categories, 'Show All')
    print (links_to_filter_pages)
    
    command_strings = get_commands_string(commands)
    
    manual_template_file = open('manualTemplate.html', 'r')
    manual_template = manual_template_file.read()
    manual_template_file.close()

    to_insert = links_to_filter_pages + command_strings
    manual_with_commands = string.replace(manual_template, '${INSERT}', to_insert)

    # create the file if it doesn't exist
    manual_file = open('manual.html', 'w+')
    manual_file.write(manual_with_commands)
    manual_file.close()
    
    for category in categories:
        command_strings = get_commands_string_for_category(commands, category)
        links_to_filter_pages = get_links_to_filter_pages(units, categories, category)
        to_insert = links_to_filter_pages + command_strings
        manual_with_commands = string.replace(manual_template, '${INSERT}', to_insert)

        # create the file if it doesn't exist
        manual_file = open(html_name(category), 'w+')
        manual_file.write(manual_with_commands)
        manual_file.close()
        
    for unit in units:
        command_strings = get_commands_string_for_unit(commands, unit)
        links_to_filter_pages = get_links_to_filter_pages(units, categories, unit)
        to_insert = links_to_filter_pages + command_strings
        manual_with_commands = string.replace(manual_template, '${INSERT}', to_insert)

        # create the file if it doesn't exist
        manual_file = open(html_name(unit), 'w+')
        manual_file.write(manual_with_commands)
        manual_file.close()
    pass

main() 
