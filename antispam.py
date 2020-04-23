#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
import codecs

from sklearn import svm
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import f1_score
from sklearn.metrics.scorer import make_scorer
from sklearn.model_selection import GridSearchCV
from sklearn.pipeline import make_pipeline
from xgboost import XGBClassifier
import subprocess
import os


def main():
    parser = argparse.ArgumentParser(description='Homework 4: antispam')
    parser.add_argument('--train', required=True)
    parser.add_argument('--test', required=True)
    parser.add_argument('--out', required=True)
    args = parser.parse_args()

    tempTest = "temp_test_antispam.csv"
    tempTrain = "temp_train_antispam.csv"

    p = "gradlew.bat" if os.name == 'nt' else "./gradlew"
    f = [p, "run", '--args="%s,%s,%s,%s"' % (args.train, args.test, tempTrain, tempTest)]
    print(f)

    print("Processing files..")
    subprocess.run(f)
    print("Processed files")

    x = []
    y = []

    with codecs.open(tempTrain, encoding='utf-8') as file:
        for line in file:
            spam, words = line.split('\t')
            x.append(words)
            y.append(int(spam))

    print("Loaded files")

    f1_scorer = make_scorer(f1_score, average="weighted")
    pipe = make_pipeline(
        TfidfVectorizer(),
        XGBClassifier()
    )
    print(svm.SVC().get_params().keys())
    param_grid = {
        'xgbclassifier__max_depth': [5],
        'xgbclassifier__learning_rate': [0.4],
        'xgbclassifier__n_estimators': [500],
        'tfidfvectorizer__max_features': [5000]
    }
    grid = GridSearchCV(pipe, param_grid, cv=5, scoring=f1_scorer)
    grid3 = grid.fit(x, y)

    print(grid3.best_score_)
    print(grid3.best_params_)

    test_x = []
    ids = []
    with codecs.open(tempTest, encoding='utf-8') as file:
        for line in file:
            t, words = line.split('\t')
            ids.append(int(t))
            test_x.append(words)

    predicted = grid3.predict(test_x)
    with codecs.open(args.out, mode="w", encoding="utf-8") as file:
        file.write("Id,Prediction\n")
        for i, v in enumerate(predicted):
            file.write(str(ids[i]))
            file.write(",")
            file.write(str(predicted[i]))
            file.write("\n")

    os.remove(tempTest)
    os.remove(tempTrain)


if __name__ == "__main__":
    main()
