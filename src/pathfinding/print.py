# import numpy as np
# import matplotlib.pyplot as plt
# import json
# import math
# import os

# obstacles = [[40, 20, math.pi / 2], [50, 80, math.pi]], [70, 50, 0]

# start = (10, 10, np.pi / 2)  # (x, y, theta)
# grid_map = np.zeros((200, 200))  # Example map (0 = free, 1 = obstacle)
# obstacle_occupancy = np.ones((30, 30))

# # Read target data from a JSON file
# with open("nodes.json", 'r') as f:
#     data = json.load(f)

# print(data)

# for obstacle in obstacles:
#     displacement = 10
#     x_start = max(0, obstacle[1] - displacement)
#     x_end = min(200, obstacle[1] + 10 + displacement)
#     y_start = max(0, obstacle[0] - displacement)
#     y_end = min(200, obstacle[0] + 10 + displacement)

#     grid_map[x_start:x_end, y_start:y_end] = obstacle_occupancy[:(x_end-x_start), :(y_end-y_start)]

# # Assuming obstacles and grid_map are already defined elsewhere
# for obstacle in obstacles:
#     displacement = 0
#     x_start = max(0, obstacle[1] - displacement)
#     x_end = min(200, obstacle[1] + 10 + displacement)
#     y_start = max(0, obstacle[0] - displacement)
#     y_end = min(200, obstacle[0] + 10 + displacement)
#     grid_map[x_start:x_end, y_start:y_end] = np.full((40, 40), 2)[:(x_end - x_start), :(y_end - y_start)]

# plt.figure(figsize=(10, 10))
# plt.imshow(grid_map, cmap='gray_r', interpolation='nearest')
# plt.colorbar(label='Grid Value (0 = Free, 1 = Obstacle)')

# # Add x and y labels
# plt.xticks(np.arange(0, 201, step=10))
# plt.yticks(np.arange(0, 201, step=10))
# plt.grid(True)

# # Label the axes
# plt.xlabel('X')
# plt.ylabel('Y')
# plt.gca().invert_yaxis()

# # Plot path data from the JSON file
# if len(data) > 1:
#     for i in range(1, len(data)):
#         # Draw a line from the previous point to the current point
#         plt.plot([data[i-1]['x']+5, data[i]['x']+5], [data[i-1]['y']+5, data[i]['y']+5], marker='o', color='b', markersize=2)

#         # Draw an arrow in the direction of theta
#         plt.arrow(data[i]['x']+5, data[i]['y']+5, np.cos(data[i]['theta']), np.sin(data[i]['theta']),
#                   head_width=1, head_length=1, fc='r', ec='r')

# # Title and show plot
# plt.title('200x200 Grid with Obstacles (Black = Obstacle, White = Free)')
# plt.show()


import numpy as np
import matplotlib.pyplot as plt
import json
import math

# Define obstacles correctly as a list of lists
obstacles = [[40, 50, - math.pi/2 ], [70, 90, -math.pi / 2], [90, 30, 0], [140, 60, math.pi], [130, 100, -math.pi]]

start = (15, 10, np.pi / 2)  # (x, y, theta)
#start = (20, 10, 0)  # (x, y, theta)
grid_map = np.zeros((200, 200))  # Example map (0 = free, 1 = obstacle)
obstacle_occupancy = np.ones((30, 30))

# Read target data from a JSON file
with open("nodes.json", 'r') as f:
    data = json.load(f)

print(data)

for obstacle in obstacles:
    displacement = 10
    x_start = max(0, obstacle[1] - displacement)
    x_end = min(200, obstacle[1] + 10 + displacement)
    y_start = max(0, obstacle[0] - displacement)
    y_end = min(200, obstacle[0] + 10 + displacement)

    grid_map[x_start:x_end, y_start:y_end] = obstacle_occupancy[:(x_end-x_start), :(y_end-y_start)]

# Assuming obstacles and grid_map are already defined elsewhere
for obstacle in obstacles:
    displacement = 0
    x_start = max(0, obstacle[1] - displacement)
    x_end = min(200, obstacle[1] + 10 + displacement)
    y_start = max(0, obstacle[0] - displacement)
    y_end = min(200, obstacle[0] + 10 + displacement)
    grid_map[x_start:x_end, y_start:y_end] = np.full((40, 40), 2)[:(x_end - x_start), :(y_end - y_start)]

plt.figure(figsize=(10, 10))
plt.imshow(grid_map, cmap='gray_r', interpolation='nearest')
plt.colorbar(label='Grid Value (0 = Free, 1 = Obstacle)')

# Add x and y labels
plt.xticks(np.arange(0, 201, step=10))
plt.yticks(np.arange(0, 201, step=10))
plt.grid(True)

# Label the axes
plt.xlabel('X')
plt.ylabel('Y')
plt.gca().invert_yaxis()

# Plot path data from the JSON file
if len(data) > 1:
    for i in range(1, len(data)):
        # Draw a line from the previous point to the current point
        plt.plot([data[i-1]['x']+5, data[i]['x']+5], [data[i-1]['y']+5, data[i]['y']+5], marker='o', color='b', markersize=2)

        # Draw an arrow in the direction of theta
        plt.arrow(data[i]['x']+5, data[i]['y']+5, np.cos(data[i]['theta']), np.sin(data[i]['theta']),
                  head_width=1, head_length=1, fc='r', ec='r')

# Title and show plot
plt.title('200x200 Grid with Obstacles (Black = Obstacle, White = Free)')
plt.show()
