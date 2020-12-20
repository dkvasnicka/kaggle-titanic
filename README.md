# kaggle-titanic

This is an attempt to use an entropy-based decision tree to solve the [Titanic challenge](https://www.kaggle.com/c/titanic-gettingStarted) at Kaggle.com.

The implementation is a pretty standard one, with one exception: it uses a columnar dataset. The CSV is lazily loaded into colums instead of reading it simply as a set of vectors. This makes entropy computation pretty fast.

Refer to `src/kaggle_titanic/example.clj` for an overview of how to use the thing.
