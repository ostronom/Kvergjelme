(defproject kvergjelme-client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main kvergjelme-client.core
  :aot [kvergjelme-client.core]
  :repositories [["xuggle repo" "http://xuggle.googlecode.com/svn/trunk/repo/share/java/"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.zip "0.1.0"]
                 ;[org.clojars.remleduff/javacv "20100416"]
                 [xuggle/xuggle-xuggler "5.4"]])
