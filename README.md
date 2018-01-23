# com.dotcms.libsass



## OS-X 
brew install libsass


## Linux
mkdir /opt/sass  
cd /opt/sass  

```
yum groupinstall "Development Tools"  
git clone https://github.com/hcatlin/sassc.git  
git clone https://github.com/hcatlin/libsass.git
vi sassc/Makefile  
```
 and add to top
```
export SASS_LIBSASS_PATH=/opt/sass/libsass  
```
``
cd sassc  
make  
ln -s /opt/sass/sassc/bin/sassc /usr/bin/sassc 
``

test:

`sassc -h  `
