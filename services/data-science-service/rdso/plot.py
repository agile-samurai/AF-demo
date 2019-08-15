from bokeh.plotting import figure, show, output_file
from bokeh.plotting import ColumnDataSource
from bokeh.embed import json_item
from bokeh.io import export_png
from bokeh.models.markers import CircleX
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
    """Uses the full dataset, represents all movies in dataset using vectorized
    placement and colorized by genre.

    Parameters
    ----------
    mdf : Pandas DataFrame
        A DataFrame merged between the movies df and the omdb data

    Returns
    -------
    bokeh.Figure
        The figure object

    """

    # for now, this populates random values, but eventually, this should use the
    # first two principal components of the Doc2Vec model to populate the x and y vars
    mdf["x"] = pd.np.random.random(size=len(mdf)) * 10000
    mdf["y"] = pd.np.random.random(size=len(mdf)) * 10000

    colormap = dict(
        zip(mdf.primary_genre.unique(), Category20[len(mdf.primary_genre.unique())])
    )
    mdf["color"] = mdf.primary_genre.map(lambda j: colormap[j])

    source = ColumnDataSource(
        data=dict(
            x=mdf.x,
            y=mdf.y,
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


def sc_plot_for_one(mdf, imdbID):
    item = mdf.loc[mdf.imdb_id == imdbID].to_dict(orient="records")[0]
    colormap = dict(
        zip(mdf.primary_genre.unique(), Category20[len(mdf.primary_genre.unique())])
    )
    genre_of_item = item["primary_genre"]
    color_of_item = colormap[item["primary_genre"]]
    everything_else = "gray"
    grayed_out_colormap = dict(zip(colormap.keys(), ["gray"] * len(colormap)))
    grayed_out_colormap[genre_of_item] = color_of_item

    mdf["color"] = mdf.primary_genre.map(lambda x: grayed_out_colormap[x])

    p = sc_plot_genre_colors(mdf, grayed_out_colormap)

    p.circle_x(
        x=item["random_x"],
        y=item["random_y"],
        size=20,
        fill_color=color_of_item,
        line_color="red",
    )
    return p
