name := "KvergjelmeClient"

version := "0.1"

scalaVersion := "2.9.2"

resolvers ++= Seq(
	"Sonatype OSS Snapshots Repository" at "http://oss.sonatype.org/content/groups/public/",
	"NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/",
	"xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/"
)

libraryDependencies ++= Seq(
	"org.scala-lang" % "scala-compiler" % "2.9.2",
	"org.scala-lang" % "scala-swing" % "2.9.2",
	"com.nativelibs4java" % "javacl" % "1.0.0-RC2",
	"com.nativelibs4java" % "scalacl" % "0.2",
	"xuggle" % "xuggle-xuggler" % "5.4"
)

autoCompilerPlugins := true

addCompilerPlugin("com.nativelibs4java" % "scalacl-compiler-plugin" % "0.2")