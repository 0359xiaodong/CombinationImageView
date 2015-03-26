# CombinationImageView
仿微信群聊头像控件


如何使用
------
### 0x1 <br >
下载工程，导入eclipse，并且在项目中引用
### 0x2 <br > 
在布局文件中使用com.dirs.combinationimageview.CombinationImageView来引入控件<br>
例如
``` html
<com.dirs.combinationimageview.CombinationImageView
      android:id="@+id/civ"
      android:layout_width="200dp"
      android:layout_height="200dp" 
/>
```
### 0x3 <br>
在程序里面，调用CombinationImageView的`loadImg`方法加载图片，该方法接受一个`vector`对象，内容为图片的本地路径。

Warring
------
目前图片的数量只能小于等于9张，如果超出9张图片，loadImg方法会抛出异常。



[欢迎通过博客相互学习](http://dirs0xcode.blogspot.com/)
