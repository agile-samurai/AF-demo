from bokeh.plotting import figure, show, output_file
from bokeh.embed import json_item
from bokeh.io import export_png
import numpy as np


def make_test_image(n):
    N = n
    x = np.random.random(size=N) * 100
    y = np.random.random(size=N) * 100
    radii = np.random.random(size=N) * 1.5
    colors = [
        "#%02x%02x%02x" % (int(r), int(g), 150) for r, g in zip(50 + 2 * x, 30 + 2 * y)
    ]

    TOOLS = "hover,crosshair,pan,wheel_zoom,zoom_in,zoom_out,box_zoom,undo,redo,reset,tap,save,box_select,poly_select,lasso_select,"

    p = figure(tools=TOOLS)

    p.scatter(x, y, radius=radii, fill_color=colors, fill_alpha=0.6, line_color=None)

    return p


def jsonify_image(p):
    return json_item(p)


def save_image(p, filename, format="png"):
    if format == "png":
        export_png(p, filename=filename)


def sc_plot_genre_colors(mdf):
    colormap = dict(
        zip(mdf.primary_genre.unique(), Category20[len(mdf.primary_genre.unique())])
    )
    mdf["color"] = mdf.primary_genre.map(lambda x: colormap[x])

    source = ColumnDataSource(
        data=dict(
            x=mdf.random_x,
            y=mdf.random_y,
            name=mdf.name,
            year=mdf.year,
            genre=mdf.primary_genre,
            color=mdf.color,
        )
    )

    TOOLTIPS = [("name", "@name"), ("year", "@year"), ("genre", "@genre")]

    p = figure(tooltips=TOOLTIPS, title="Movie Clusters")

    p.circle("x", "y", size=12, alpha=0.6, color="color", source=source)
    return p
