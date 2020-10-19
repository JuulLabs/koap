#!/bin/sh
git tag | grep -E '^[0-9]' | sort -V | tail -1

