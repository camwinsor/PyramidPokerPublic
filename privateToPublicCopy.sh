#!/usr/bin/env bash

rsync --quiet -rv --exclude=.git --exclude=.gitignore --exclude=privateToPublicCopy.sh --delete /Users/CWins/AndroidStudioProjects/AndroidFlashPoker/ /Users/CWins/source/Pyramid/PyramidPokerPublic/
git add -A
git commit -m "sync from private repo"
git push origin master

