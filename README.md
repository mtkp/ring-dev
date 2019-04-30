mtkp/ring-dev
=============

Run a development ring server with a `deps.edn` project.

This tool is designed to fill a gap when replacing leiningen with tools.deps.alpha
if previously relying on the useful
[lein-ring](https://github.com/weavejester/lein-ring) plugin.
ring-dev enables basic functionality similar to `lein ring server-headless`.
Currently there are no plans to mimic the packaging commands of lein-ring
(e.g. `lein ring uberwar`).

# Usage

Add an alias to `deps.edn`

```clj
{...
 :aliases
 {:server {:extra-deps {mtkp/ring-dev {:git/url "https://github.com/mtkp/ring-dev"
                                       :sha "...."}}
           :main-opts ["-m" "mtkp.ring-dev.main"
                       "your.namespace/your-server-handler"]}}}
```

Then start a server using the alias added to `deps.edn`

```sh
clj -Aserver
```

By default the server is started on port 8000; including `"--port" "40404"`
in the `:main-opts` will change the port to the specified value
(in this case, 40404).

Run with `--help` to see a list of defaults and additional options (`--help` will not
start the server).

```sh
clj -Aserver --help
```

See `example/` for a working example.
