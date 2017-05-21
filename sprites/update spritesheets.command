#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd "$DIR"
TexturePacker Caveland.tps
TexturePacker "main character.tps"