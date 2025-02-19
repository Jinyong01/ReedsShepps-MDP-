import pygame
import numpy as np
import math
import json
import os

# Constants
GAME_SIZE = 200
GRID_SIZE = 20
SIMULATION_RATIO = GAME_SIZE/GRID_SIZE
SCALE_RATIO = 2
OBSTACLE_SIZE = 1
OBSTACLE_OFFSET = 1.5
CELL_SIZE = 30
GRID_WINDOW_SIZE = GRID_SIZE * CELL_SIZE
SUB_WINDOW_SIZE_X = 200
SUB_WINDOW_SIZE_Y = GRID_WINDOW_SIZE
SCREEN_OFFSET_SIZE = 10
SCREEN_WINDOW_SIZE_X = GRID_SIZE * CELL_SIZE + SCREEN_OFFSET_SIZE*(2+1) + SUB_WINDOW_SIZE_X
SCREEN_WINDOW_SIZE_Y = GRID_SIZE * CELL_SIZE + SCREEN_OFFSET_SIZE*2
WINDOW_COLOR = (160, 160, 160)
BG_COLOR = (255, 255, 255)
GRID_COLOR = (220, 220, 220)
OBSTACLE_COLOR = (0, 0, 0)
OBSTACLE_OFFSET_COLOR = (200,200,200)
TARGET_POSITION_COLOR = (255,0,0)
OPTIMAL_PATH_COLOR = (0,0,255)
ROBOT_BORDER_COLOR = (100, 100, 100)


# Function to position obstacles in an array/grid
def update_grid():
    for x, y, theta in obstacles:

        # Calculate the position of the offset region (scaled)
        offset_x_start = (x - OBSTACLE_OFFSET) * SCALE_RATIO
        offset_y_start = (y - OBSTACLE_OFFSET) * SCALE_RATIO
        offset_x_end = (x + OBSTACLE_SIZE + OBSTACLE_OFFSET) * SCALE_RATIO
        offset_y_end = (y + OBSTACLE_SIZE + OBSTACLE_OFFSET) * SCALE_RATIO

        # Mark offset region in array
        for i in range(int(offset_x_start), int(offset_x_end)):
            for j in range(int(offset_y_start), int(offset_y_end)):
                if grid[j, i] == 0:
                    grid[j, i] = 2

        # Mark obstacle in array
        for i in range(int(x * SCALE_RATIO), int((x + OBSTACLE_SIZE) * SCALE_RATIO)):
            for j in range(int(y * SCALE_RATIO), int((y + OBSTACLE_SIZE) * SCALE_RATIO)):
                grid[j, i] = 1

def draw_robot(x, y, theta):
    x += 1.5
    y += 1.5
    y = GRID_SIZE - y
    rect_width = 3 * CELL_SIZE
    rect_height = 3 * CELL_SIZE

    # Create a surface to draw the rectangle
    rect_surface = pygame.Surface((rect_width, rect_height), pygame.SRCALPHA)
    rect_surface.fill((0, 0, 0, 0))  # Transparent background

    # Draw the black border
    pygame.draw.rect(rect_surface, ROBOT_BORDER_COLOR, (0, 0, rect_width, rect_height), 3)  # Border thickness = 3px

    # Rotate the surface
    rotated_surface = pygame.transform.rotate(rect_surface, -theta)  # Pygame rotates counterclockwise

    # Get the new rectangle bounding box
    rotated_rect = rotated_surface.get_rect(center=(x*CELL_SIZE, y*CELL_SIZE))

    # Blit the rotated rectangle onto the screen
    grid_surface.blit(rotated_surface, rotated_rect.topleft)

def draw_path(paths):
    for i in range(len(paths)-1):
        if len(paths) < 2: break
        x1 = paths[i]['x']
        y1 = paths[i]['y']
        x1 = x1 / SIMULATION_RATIO
        flipped_y1 = (GRID_SIZE - y1/SIMULATION_RATIO - 1)
        x2 = paths[i+1]['x']
        y2 = paths[i+1]['y']
        flipped_y2 = (GRID_SIZE - y2/SIMULATION_RATIO - 1)
        x2 = x2 / SIMULATION_RATIO
            
            # Convert grid coordinates to pixel coordinates (using the center of the cell)
        p1 = (x1 * CELL_SIZE + CELL_SIZE/2, flipped_y1 * CELL_SIZE + CELL_SIZE/2)
        p2 = (x2 * CELL_SIZE + CELL_SIZE/2, flipped_y2 * CELL_SIZE + CELL_SIZE/2)
            
        pygame.draw.line(grid_surface, OPTIMAL_PATH_COLOR, p1, p2, 2)


    #for path in paths:
    #    if len(path) < 2: break
    #    for i in range(len(path) - 1):
    #        x1, y1, theta1, action1 = path[i]
    #        x1 = x1 / SIMULATION_RATIO
    #        flipped_y1 = (GRID_SIZE - y1/SIMULATION_RATIO - 1)
    #        x2, y2, theta2, action2 = path[i+1]
    #        flipped_y2 = (GRID_SIZE - y2/SIMULATION_RATIO - 1)
    #        x2 = x2 / SIMULATION_RATIO
    #        
    #        # Convert grid coordinates to pixel coordinates (using the center of the cell)
    #        p1 = (x1 * CELL_SIZE + CELL_SIZE/2, flipped_y1 * CELL_SIZE + CELL_SIZE/2)
    #        p2 = (x2 * CELL_SIZE + CELL_SIZE/2, flipped_y2 * CELL_SIZE + CELL_SIZE/2)
    #        
    #        pygame.draw.line(grid_surface, OPTIMAL_PATH_COLOR, p1, p2, 2)

# Function to draw simulation layout
def draw_grid():
    update_grid()

    # Add Obstacles and Offset
    for y in range(GRID_SIZE*SCALE_RATIO):
        for x in range(GRID_SIZE*SCALE_RATIO):
            flipped_y = (GRID_SIZE * SCALE_RATIO - 1) - y
            rect = pygame.Rect(x * CELL_SIZE / SCALE_RATIO, flipped_y * CELL_SIZE / SCALE_RATIO, CELL_SIZE / SCALE_RATIO, CELL_SIZE / SCALE_RATIO)
            if grid[y, x] == 1:
                pygame.draw.rect(grid_surface, OBSTACLE_COLOR, rect)
            elif grid[y, x] == 2:
                pygame.draw.rect(grid_surface, OBSTACLE_OFFSET_COLOR, rect)
    
    # Add Grid Lines
    for y in range(GRID_SIZE):
        for x in range(GRID_SIZE):
            rect = pygame.Rect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
            pygame.draw.rect(grid_surface, GRID_COLOR, rect, 1)

    # Add a Target Face to Obstacle Edge
    for x, y, theta in obstacles:
        flipped_y = GRID_SIZE - y - 1
        if theta == 0: pygame.draw.line(grid_surface, (255, 0, 0), ((OBSTACLE_SIZE+x) * CELL_SIZE, flipped_y * CELL_SIZE), ((OBSTACLE_SIZE+x) * CELL_SIZE, (flipped_y+OBSTACLE_SIZE) * CELL_SIZE), 5)
        if theta == math.pi/2: pygame.draw.line(grid_surface, (255, 0, 0), (x * CELL_SIZE, (flipped_y) * CELL_SIZE), ((OBSTACLE_SIZE+x) * CELL_SIZE, (flipped_y) * CELL_SIZE), 5)
        if theta == math.pi: pygame.draw.line(grid_surface, (255, 0, 0), (x * CELL_SIZE, flipped_y * CELL_SIZE), (x * CELL_SIZE, (flipped_y+OBSTACLE_SIZE) * CELL_SIZE), 5)
        if theta == -math.pi/2: pygame.draw.line(grid_surface, (255, 0, 0), (x * CELL_SIZE, (flipped_y+OBSTACLE_SIZE) * CELL_SIZE), ((OBSTACLE_SIZE+x) * CELL_SIZE, (flipped_y+OBSTACLE_SIZE) * CELL_SIZE), 5)
    
    # Mark Target Position to Reach
    for x, y, theta in targets:
        flipped_y = GRID_SIZE - y - 1
        top_left = ((x + 0.2) * CELL_SIZE, (flipped_y + 0.2) * CELL_SIZE)
        bottom_right = ((x + 1 - 0.2) * CELL_SIZE, (flipped_y + 1 - 0.2) * CELL_SIZE)
        pygame.draw.line(grid_surface, TARGET_POSITION_COLOR, (top_left[0], top_left[1]), (bottom_right[0], bottom_right[1]), 2)
        pygame.draw.line(grid_surface, TARGET_POSITION_COLOR, (bottom_right[0], top_left[1]), (top_left[0], bottom_right[1]), 2)

def normalize_angle(theta):
    return (theta + 2 * math.pi) % (2 * math.pi)

# Main Loop
json_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "full_path.json")
with open(json_path, "r") as file:
    paths = json.load(file)  # Load JSON into a Python list of dictionaries

test_obstacles = [[4, 5, - math.pi/2 ], 
                  [4, 16, -math.pi / 2], 
                  [15, 16, math.pi], 
                  [15, 5, math.pi], 
                  [9, 11, -math.pi]]
test_targets = []
for x, y, theta in test_obstacles:
    distance = 1+1
    target_x = x - distance * math.cos(theta + math.pi)  # Moving in the opposite direction
    target_y = y - distance * math.sin(theta + math.pi)
    print(x)
    print(y)
    print(target_x)
    print(target_y)
    target_theta = normalize_angle(theta + math.pi)
    test_targets.append([target_x, target_y, target_theta])

obstacles = test_obstacles
targets = test_targets
grid = np.zeros((GRID_SIZE * SCALE_RATIO, GRID_SIZE * SCALE_RATIO))
pygame.init()
clock = pygame.time.Clock()
screen = pygame.display.set_mode((SCREEN_WINDOW_SIZE_X, SCREEN_WINDOW_SIZE_Y))
grid_surface = pygame.Surface((GRID_WINDOW_SIZE, GRID_WINDOW_SIZE))
sub_surface = pygame.Surface((SUB_WINDOW_SIZE_X, SUB_WINDOW_SIZE_Y))
pygame.display.set_caption("Robot Movement Area Simulator")
running = True
while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    screen.fill(WINDOW_COLOR)
    grid_surface.fill(BG_COLOR)
    sub_surface.fill(BG_COLOR)
    draw_grid()
    draw_path(paths)
    draw_robot(1,1,0)
    screen.blit(grid_surface, (SCREEN_OFFSET_SIZE, SCREEN_OFFSET_SIZE))
    screen.blit(sub_surface, (SCREEN_OFFSET_SIZE*2 + GRID_WINDOW_SIZE, SCREEN_OFFSET_SIZE))
    pygame.display.flip()

pygame.quit()
