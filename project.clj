(defproject sh.roosta.gallery "1.2.1"
  :description "Gallery site"
  :url "http://gallery.roosta.sh"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.6.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [cljsjs/photoswipe "4.1.1-0"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [cljsjs/react-grid-layout "0.13.7-0"
                  :exclusions [cljsjs/react
                               cljsjs/react-dom]]
                 [org.clojure/core.async "0.2.391"
                  :exclusions [org.clojure/tools.reader]]
                 [reagent "0.6.0"]]

  :plugins [[lein-garden "0.3.0"]
            [lein-figwheel "0.5.8"]
            [lein-asset-minifier "0.3.0"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src/styles"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "resources/public/css/compiled"
                                    "prod/css"
                                    "prod/js"
                                    "target"]

  :minify-assets
  {:assets
   {"prod/css/screen.min.css" "resources/public/css/compiled/screen.css"
    "prod/css/grid-layout.min.css" "resources/public/css/grid-layout.css"
    "prod/css/normalize.min.css" "resources/public/css/normalize.css"
    "prod/css/photoswipe.min.css" "resources/public/css/photoswipe.css"
    "prod/css/default-skin.min.css" "resources/public/css/default-skin.css"
    }}

  :garden {:builds [{;; Optional name of the build:
                     :id "dev"
                     ;; Source paths where the stylesheet source code is
                     :source-paths ["src/styles"]
                     ;; The var containing your stylesheet:
                     :stylesheet sh.roosta.gallery.core/screen
                     ;; Compiler flags passed to `garden.core/css`:
                     :compiler {;; Where to save the file:
                                :output-to "resources/public/css/compiled/screen.css"
                                ;; Compress the output?
                                :pretty-print? true}}]}

  :aliases {"dev" ^{:doc "Clean and auto compile garden style"}
            ["do"
             ["clean"]
             ["garden" "auto"]]
            "prod" ^{:doc "Clean, minify and compile"}
            ["do"
             ["clean"]
             ["garden" "once"]
             ["minify-assets"]
             ["cljsbuild" "once" "min"]]}

  :prep-tasks [["garden" "once"]]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]

                ;; the presence of a :figwheel configuration here
                ;; will cause figwheel to inject the figwheel client
                ;; into your build
                :figwheel {:on-jsload "sh.roosta.gallery.core/on-js-reload" }

                :compiler {:main sh.roosta.gallery.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/sh/roosta/gallery.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                           ;; https://github.com/binaryage/cljs-devtools
                           :preloads [devtools.preload]}}
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "min"
                :source-paths ["src/cljs"]
                :compiler {:output-to "prod/js/gallery.js"
                           :main sh.roosta.gallery.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"] ;; watch and update CSS
             :open-file-command "emacs-file-opener" }


  :profiles {:dev {:dependencies [[binaryage/devtools "0.8.2"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   ;; include script to use repl.clj to start figwheel and cljs repl
                   :source-paths ["src/cljs" "dev" "script"]
                   ;; for CIDER
                   ; :plugins [[cider/cider-nrepl "0.14.0-SNAPSHOT"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

)
