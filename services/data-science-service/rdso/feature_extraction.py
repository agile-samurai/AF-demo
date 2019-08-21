import pandas as pd
import numpy as np
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE

from vectorize import get_tagged_corpus, get_vectors


def principal_components_analysis(df, corpus, model):
    matrix = list(model.docvecs)
    pca = PCA(n_components=2)
    vecs = pca.fit_transform(matrix)
    tlist = [x.tags[0] for x in corpus]
    if len(tlist) == len(matrix):
        mdf = pd.DataFrame(index=tlist, data=vecs)
        df.set_index("film_id").merge(mdf)

    pass
