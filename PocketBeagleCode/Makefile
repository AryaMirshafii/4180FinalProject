TARGET = CloudPusher.exe
LIBS = -lm -lcurl
CC = g++
CPPFLAGS = -lpthread -std=c++11 -g -Wall -Wextra -O3

.PHONY: default all tags debug scrub clean run

default: $(TARGET)
all: default

SOURCE_DIR = ./
HEADER_DIR = ./

OBJECTS = $(patsubst %.cpp, %.o, $(wildcard $(SOURCE_DIR)*.cpp))
HEADERS = $(wildcard $(HEADER_DIR)*.h)

%.o: %.cpp $(HEADERS)
	$(CC) $(CPPFLAGS) -c $< -o $@

.PRECIOUS: $(TARGET) $(OBJECTS)

$(TARGET): $(OBJECTS)
	$(CC) $(OBJECTS) -lpthread -Wall $(LIBS) -o $@

tags:
	ctags -Rf .tags *

debug:
	make
	gdb $(TARGET)

scrub:
	-rm -f *.o
	-rm -f $(TARGET)
	-rm -f ./headers/*.o
	-rm -f .tags
	-rm -f tags

clean:
	-rm -f *.o
	-rm -f $(TARGET)
	-rm -f ./headers/*.o
	-rm -f ./src/*.o

run:
	./$(TARGET)
