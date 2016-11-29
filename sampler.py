'''
Created on Nov 28, 2016

@author: hbd
'''

from shutil import copyfile
import os
import random

if __name__ == '__main__':
    flist=os.listdir('cacm')
    fsample=random.sample(flist,int(len(flist)*0.02))
    if not os.path.exists('samplecacm'):
        os.makedirs('samplecacm')
    for page in fsample:
        copyfile('cacm/'+page, 'samplecacm/'+page)
    