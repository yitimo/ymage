# Change Logs of Ymage

## v1.3.9

### Feature

1. *Grider* When set grid item size to 0 with single item src, only gif tag will be shown(long image tag will not show any more).
2. *Grider* Update long image rule, change to ``height > width * 3``.

## v1.3.7

### Fix

1. *Browser* Use Glide.loadFile() for image browser adapter. (This can make scroll more smooth.)

### Feature

1. *Browser* Image browser support thumb images.

### Refactor

1. *Common* Update glide engine methods.

## v1.3.6

Stable version.

1. Image browser support gif/long image tag.
2. Image picker support camera at first position.
3. Image cutter support scale and rotate.
4. Image grider support single image, 2*2 grid and 3*3 grid.
