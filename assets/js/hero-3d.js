/*
 * 히어로 3D 무대 — 선택 정렬을 실제 3차원 공간에서 재생한다.
 *
 * 이 파일 하나만 ES 모듈이다(사이트의 나머지 브라우저 JS는 ES5 문법만 쓴다).
 * three.js가 ES 모듈로만 배포되기 때문이고, "three" 이름을 실제 URL로
 * 이어 주는 import map은 index.html의 <head>에 있다.
 *
 * 역할 경계가 하나 있다: 정렬 알고리즘의 단계(frame)는 landing.js가
 * 계산하고, 이 파일은 그 frame을 3D로 "그리는 방법"만 안다. 그래서 2D
 * 막대와 3D 무대가 같은 프레임 배열을 공유하고, 둘 중 무엇이 뜨든
 * 화면에 보이는 알고리즘 동작은 완전히 같다.
 *
 * 이 모듈이 로드되지 않거나(CDN 차단·오프라인) WebGL이 없으면
 * window.AllHero3D가 없거나 create()가 null을 돌려준다. landing.js는
 * 그때 기존 2D 막대로 그대로 그린다 — 3D는 어디까지나 향상이지 전제가 아니다.
 */
import * as THREE from "three";

/* ---------- 무대 치수 ----------
   막대 하나의 폭·깊이와 간격. 값을 바꾸면 카메라 프레이밍(fitDistance)이
   따라 계산되므로 여기만 만지면 된다. */
var BAR_W = 0.66;
var BAR_D = 0.66;
var PITCH = 0.94;      /* 막대 중심 간 거리 */
var MAX_H = 4.0;       /* 가장 큰 값의 높이 */
var MIN_H = 0.34;      /* 가장 작은 값도 이 아래로는 안 내려간다 */
var SWAP_MS = 460;     /* 자리 교대에 쓰는 시간 */

/* 상태색과 잉크는 CSS 토큰이 단일 출처다. 테마가 바뀌면 다시 읽는다. */
var TOKENS = [
    "--surface", "--surface-2", "--line", "--line-strong", "--ink", "--ink-faint",
    "--state-compare", "--state-done", "--state-visit",
    "--glyph-ink-warn", "--glyph-ink-done"
];

function readTokens() {
    var cs = getComputedStyle(document.documentElement);
    var out = {};
    for (var i = 0; i < TOKENS.length; i += 1) {
        var raw = (cs.getPropertyValue(TOKENS[i]) || "").trim();
        out[TOKENS[i]] = raw || "#888888";
    }
    return out;
}

function hasWebGL() {
    try {
        var canvas = document.createElement("canvas");
        return Boolean(window.WebGLRenderingContext &&
            (canvas.getContext("webgl2") || canvas.getContext("webgl")));
    } catch (e) {
        return false;
    }
}

function easeInOut(t) {
    return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
}

/* 바닥은 사각형 그대로 두면 카드 안에서 잘린 판때기로 보인다.
   가장자리를 알파로 녹여 "떠 있는 무대"로 만든다. */
function radialAlphaTexture() {
    var canvas = document.createElement("canvas");
    canvas.width = 256;
    canvas.height = 256;
    var ctx = canvas.getContext("2d");
    var g = ctx.createRadialGradient(128, 128, 0, 128, 128, 128);
    g.addColorStop(0, "#ffffff");
    g.addColorStop(0.42, "#f2f2f2");
    g.addColorStop(0.72, "#6e6e6e");
    g.addColorStop(1, "#000000");
    ctx.fillStyle = g;
    ctx.fillRect(0, 0, 256, 256);
    var tex = new THREE.CanvasTexture(canvas);
    tex.colorSpace = THREE.NoColorSpace;   /* 알파맵은 색이 아니라 값이다 */
    return tex;
}

/* 값 라벨. 3D 안의 글자는 배경을 예측할 수 없으므로 알약형 판 위에 올린다 —
   막대 색이 무엇이든 글자 대비가 카드 표면과 같은 조건으로 고정된다. */
function makeLabel() {
    var canvas = document.createElement("canvas");
    canvas.width = 224;
    canvas.height = 112;
    var tex = new THREE.CanvasTexture(canvas);
    tex.colorSpace = THREE.SRGBColorSpace;
    var material = new THREE.SpriteMaterial({
        map: tex, transparent: true, depthWrite: false, depthTest: false
    });
    var sprite = new THREE.Sprite(material);
    /* 막대 간격(PITCH)보다 좁게 — 뒤쪽 막대가 원근으로 좁아졌을 때
       이웃 라벨과 겹치지 않는 선이다. */
    sprite.scale.set(0.78, 0.39, 1);
    sprite.renderOrder = 10;
    return { canvas: canvas, texture: tex, sprite: sprite, key: "" };
}

function drawLabel(label, text, ink, plate, edge) {
    var key = text + "|" + ink + "|" + plate + "|" + edge;
    if (label.key === key) return;      /* 매 프레임 캔버스를 다시 칠하지 않는다 */
    label.key = key;

    var ctx = label.canvas.getContext("2d");
    var w = label.canvas.width;
    var h = label.canvas.height;
    ctx.clearRect(0, 0, w, h);

    var r = 30;
    var pad = 6;
    ctx.beginPath();
    ctx.moveTo(pad + r, pad);
    ctx.arcTo(w - pad, pad, w - pad, h - pad, r);
    ctx.arcTo(w - pad, h - pad, pad, h - pad, r);
    ctx.arcTo(pad, h - pad, pad, pad, r);
    ctx.arcTo(pad, pad, w - pad, pad, r);
    ctx.closePath();
    ctx.fillStyle = plate;
    ctx.fill();
    ctx.lineWidth = 4;
    ctx.strokeStyle = edge;
    ctx.stroke();

    ctx.font = "700 52px 'JetBrains Mono', Consolas, 'Courier New', monospace";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillStyle = ink;
    ctx.fillText(text, w / 2, h / 2 + 2);

    label.texture.needsUpdate = true;
}

/*
 * create(container, options) → 무대 핸들 또는 null
 *   options.reducedMotion  애니메이션 축소 설정. 카메라 드리프트를 끈다.
 *   options.maxValue       높이 정규화 기준값
 *
 * 반환 핸들
 *   setValues(values)  막대를 새로 세운다 (값은 서로 달라야 한다 — 값으로
 *                      막대를 식별해 자리 교대를 애니메이션한다)
 *   setFrame(frame)    landing.js가 만든 단계 하나를 무대에 반영
 *   destroy()          GPU 자원 해제
 */
function create(container, options) {
    if (!container || !hasWebGL()) return null;

    var opts = options || {};
    var reducedMotion = Boolean(opts.reducedMotion);
    var maxValue = opts.maxValue || 44;

    var renderer;
    try {
        renderer = new THREE.WebGLRenderer({
            antialias: true, alpha: true, powerPreference: "low-power"
        });
    } catch (e) {
        return null;
    }

    renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.setClearAlpha(0);
    renderer.domElement.classList.add("hero-viz__canvas");
    container.appendChild(renderer.domElement);

    var scene = new THREE.Scene();
    var camera = new THREE.PerspectiveCamera(38, 1.6, 0.1, 100);

    var tokens = readTokens();

    /* ---------- 빛 ----------
       세 개면 충분하다: 하늘/땅 반사(hemi), 그림자를 만드는 주광(key),
       뒤에서 윤곽을 살리는 보조광(rim). */
    var hemi = new THREE.HemisphereLight(0xffffff, 0x223044, 1.15);
    scene.add(hemi);

    /* 주광은 너무 높이 두지 않는다 — 바로 위에서 내리쬐면 그림자가 막대 밑에
       깔려 보이지 않는다. 옆에서 비스듬히 넣어야 접지 그림자가 옆으로 뻗는다. */
    var key = new THREE.DirectionalLight(0xffffff, 2.4);
    key.position.set(5.4, 6.2, 4.6);
    key.castShadow = true;
    key.shadow.mapSize.set(1024, 1024);
    key.shadow.camera.near = 1;
    key.shadow.camera.far = 26;
    key.shadow.camera.left = -7;
    key.shadow.camera.right = 7;
    key.shadow.camera.top = 7;
    key.shadow.camera.bottom = -7;
    key.shadow.bias = -0.0012;
    key.shadow.normalBias = 0.02;
    scene.add(key);

    var rim = new THREE.DirectionalLight(0xffffff, 0.75);
    rim.position.set(-5.5, 3.2, -4.6);
    scene.add(rim);

    /* ---------- 바닥 ----------
       두 겹이다.
       1) 색이 있는 원판(disc) — 막대가 어디에 서 있는지 알려 주는 무대 바닥.
          가장자리를 알파로 녹여 카드 안에서 "잘린 판때기"로 보이지 않게 한다.
       2) 그림자만 받는 판(ShadowMaterial) — 접지 그림자. 라이트 테마에서는
          1)의 색 차이가 거의 없어서(표면색과 몇 단계 차이) 이 그림자가
          입체감의 거의 전부를 만든다. */
    var floorAlpha = radialAlphaTexture();
    /* 판이 너무 크면 알파 페이드가 화면 밖에서 끝나 잘린 판으로 보이고,
       너무 작으면 막대 밑동이 판 밖으로 나간다. 막대 줄 폭(약 6.6)의 두 배. */
    var floorGeo = new THREE.PlaneGeometry(13, 13);

    var floorMat = new THREE.MeshBasicMaterial({
        color: new THREE.Color(tokens["--line"]),
        transparent: true,
        alphaMap: floorAlpha,
        depthWrite: false
    });
    var floor = new THREE.Mesh(floorGeo, floorMat);
    floor.rotation.x = -Math.PI / 2;
    scene.add(floor);

    var shadowMat = new THREE.ShadowMaterial({ opacity: 0.34 });
    var shadowPlane = new THREE.Mesh(floorGeo, shadowMat);
    shadowPlane.rotation.x = -Math.PI / 2;
    shadowPlane.position.y = 0.002;      /* z-파이팅 방지 */
    shadowPlane.receiveShadow = true;
    scene.add(shadowPlane);

    /* ---------- 막대 ----------
       geometry의 원점을 밑면으로 내려 두면 scale.y가 곧 높이가 된다.
       (그렇지 않으면 높이를 바꿀 때마다 y 위치를 함께 보정해야 한다.) */
    var barGeo = new THREE.BoxGeometry(BAR_W, 1, BAR_D);
    barGeo.translate(0, 0.5, 0);
    var capGeo = new THREE.BoxGeometry(BAR_W * 1.16, 0.09, BAR_D * 1.16);

    var barsGroup = new THREE.Group();
    scene.add(barsGroup);

    /* 상태 표시는 색만으로 하지 않는다 — 최솟값은 위에서 내리꽂는 원뿔,
       비교 중은 회전하는 팔면체, 확정은 막대 위에 얹히는 판이 함께 붙는다.
       색을 못 보는 조건에서도 형태로 읽힌다. */
    var minMarker = new THREE.Mesh(
        new THREE.ConeGeometry(0.19, 0.4, 4),
        new THREE.MeshStandardMaterial({
            color: new THREE.Color(tokens["--state-visit"]),
            emissive: new THREE.Color(tokens["--state-visit"]),
            emissiveIntensity: 0.45, roughness: 0.35, metalness: 0.1
        })
    );
    minMarker.rotation.x = Math.PI;      /* 뾰족한 끝이 막대를 가리킨다 */
    minMarker.castShadow = true;
    minMarker.visible = false;
    scene.add(minMarker);

    var compareMarker = new THREE.Mesh(
        new THREE.OctahedronGeometry(0.19),
        new THREE.MeshStandardMaterial({
            color: new THREE.Color(tokens["--state-compare"]),
            emissive: new THREE.Color(tokens["--state-compare"]),
            emissiveIntensity: 0.45, roughness: 0.35, metalness: 0.1
        })
    );
    compareMarker.castShadow = true;
    compareMarker.visible = false;
    scene.add(compareMarker);

    var bars = [];
    var count = 0;
    var frame = null;
    var camDistance = 9;

    function xForIndex(index) {
        return (index - (count - 1) / 2) * PITCH;
    }

    function heightFor(value) {
        return MIN_H + (value / maxValue) * (MAX_H - MIN_H);
    }

    function disposeBars() {
        for (var i = 0; i < bars.length; i += 1) {
            var bar = bars[i];
            barsGroup.remove(bar.group);
            bar.mesh.material.dispose();
            bar.cap.material.dispose();
            bar.label.sprite.material.dispose();
            bar.label.texture.dispose();
        }
        bars = [];
    }

    function setValues(values) {
        disposeBars();
        count = values.length;

        for (var i = 0; i < count; i += 1) {
            var group = new THREE.Group();
            group.position.x = xForIndex(i);

            var material = new THREE.MeshStandardMaterial({
                color: new THREE.Color(tokens["--line-strong"]),
                emissive: new THREE.Color(0x000000),
                roughness: 0.44,
                metalness: 0.08
            });
            var mesh = new THREE.Mesh(barGeo, material);
            mesh.scale.y = heightFor(values[i]);
            mesh.castShadow = true;
            mesh.receiveShadow = true;
            group.add(mesh);

            var cap = new THREE.Mesh(capGeo, new THREE.MeshStandardMaterial({
                color: new THREE.Color(tokens["--state-done"]),
                emissive: new THREE.Color(tokens["--state-done"]),
                emissiveIntensity: 0.35,
                roughness: 0.3,
                metalness: 0.15
            }));
            cap.castShadow = true;
            cap.visible = false;
            group.add(cap);

            var label = makeLabel();
            group.add(label.sprite);

            barsGroup.add(group);
            bars.push({
                value: values[i],
                height: heightFor(values[i]),
                group: group,
                mesh: mesh,
                cap: cap,
                label: label,
                /* 자리 교대 트윈 */
                fromX: group.position.x,
                toX: group.position.x,
                tween: 1,
                targetColor: new THREE.Color(tokens["--line-strong"]),
                targetEmissive: new THREE.Color(0x000000),
                targetEmissiveIntensity: 0,
                state: "idle"
            });
        }

        layoutStatic();
        fitCamera();
        requestRender();
    }

    /* 트윈이 필요 없는 것들 — 캡·라벨 높이는 막대 높이에만 달렸다 */
    function layoutStatic() {
        for (var i = 0; i < bars.length; i += 1) {
            var bar = bars[i];
            bar.cap.position.y = bar.height + 0.045;
            bar.label.sprite.position.y = bar.height + 0.46;
        }
    }

    function setFrame(next) {
        frame = next;
        if (!frame || !bars.length) return;

        for (var i = 0; i < bars.length; i += 1) {
            var bar = bars[i];
            var index = frame.arr.indexOf(bar.value);
            if (index === -1) index = i;

            var targetX = xForIndex(index);
            if (Math.abs(targetX - bar.toX) > 0.0001) {
                bar.fromX = bar.group.position.x;
                bar.toX = targetX;
                bar.tween = reducedMotion ? 1 : 0;
                if (reducedMotion) bar.group.position.x = targetX;
            }

            var state = "idle";
            if (index <= frame.sortedUpto) state = "done";
            else if (index === frame.min) state = "min";
            else if (index === frame.compare) state = "compare";
            bar.state = state;

            if (state === "done") {
                bar.targetColor.set(tokens["--state-done"]);
                bar.targetEmissive.set(tokens["--state-done"]);
                bar.targetEmissiveIntensity = 0.3;
            } else if (state === "min") {
                bar.targetColor.set(tokens["--state-visit"]);
                bar.targetEmissive.set(tokens["--state-visit"]);
                bar.targetEmissiveIntensity = 0.34;
            } else if (state === "compare") {
                bar.targetColor.set(tokens["--state-compare"]);
                bar.targetEmissive.set(tokens["--state-compare"]);
                bar.targetEmissiveIntensity = 0.34;
            } else {
                bar.targetColor.set(tokens["--line-strong"]);
                bar.targetEmissive.set(0x000000);
                bar.targetEmissiveIntensity = 0;
            }

            bar.cap.visible = state === "done";

            var ink = tokens["--ink"];
            var edge = tokens["--line-strong"];
            if (state === "done") { ink = tokens["--glyph-ink-done"]; edge = tokens["--state-done"]; }
            else if (state === "min") { ink = tokens["--state-visit"]; edge = tokens["--state-visit"]; }
            else if (state === "compare") { ink = tokens["--glyph-ink-warn"]; edge = tokens["--state-compare"]; }
            /* 확정된 막대에는 ✓를 붙인다 — 2D 막대와 같은 글리프 규칙 */
            drawLabel(bar.label,
                state === "done" ? bar.value + " ✓" : String(bar.value),
                ink, tokens["--surface"], edge);
        }

        minMarker.visible = frame.min >= 0;
        compareMarker.visible = frame.compare >= 0;
        requestRender();
    }

    /* 막대 전체가 화면에 들어오는 카메라 거리 — 세로/가로 중 더 먼 쪽을 쓴다.
       좁은 화면(모바일)에서 가로가 잘리는 일이 없다.

       세로 범위는 바닥(0)부터 라벨·마커 꼭대기(MAX_H + 1.5)까지다. 그 한가운데를
       바라봐야 위아래 여백이 같아진다 — 바닥을 바라보면 화면 위쪽 절반이
       통째로 빈다(실제로 그렇게 났었다). */
    var CONTENT_TOP = MAX_H + 1.5;
    /* 아래로도 여유를 둔다 — 카메라가 위에서 내려다보므로 바닥(y=0)은 화면에서
       y=0보다 더 아래에 맺힌다. 이 여유가 없으면 막대 밑동이 잘린다. */
    var CONTENT_BOTTOM = -1.15;
    var lookAtY = (CONTENT_TOP + CONTENT_BOTTOM) / 2;

    function fitCamera() {
        var aspect = camera.aspect || 1.6;
        var halfW = (Math.max(count, 1) * PITCH) / 2 + 0.45;
        var halfH = (CONTENT_TOP - CONTENT_BOTTOM) / 2;
        var vFov = (camera.fov * Math.PI) / 180;
        var distH = halfH / Math.tan(vFov / 2);
        var hFov = 2 * Math.atan(Math.tan(vFov / 2) * aspect);
        var distW = halfW / Math.tan(hFov / 2);
        camDistance = Math.max(distH, distW) * 1.02;
    }

    /* ---------- 크기 ---------- */
    function resize() {
        var w = container.clientWidth;
        var h = container.clientHeight;
        if (!w || !h) return;
        renderer.setSize(w, h, false);
        camera.aspect = w / h;
        camera.updateProjectionMatrix();
        fitCamera();
        requestRender();
    }

    /* 첫 resize()는 아래 재생 루프의 상태 변수가 초기화된 뒤에 부른다 —
       resize()가 requestRender()를 타는데, 그보다 먼저 부르면 그 안에서
       세운 pendingRender를 뒤이어 실행되는 `var pendingRender = 0`이
       덮어써 rAF 하나가 미아가 된다. */
    var resizeObserver = null;
    if (window.ResizeObserver) {
        resizeObserver = new ResizeObserver(resize);
        resizeObserver.observe(container);
    } else {
        window.addEventListener("resize", resize);
    }

    /* ---------- 포인터 시차 ---------- */
    var pointerX = 0;
    var pointerY = 0;
    var pointerTargetX = 0;
    var pointerTargetY = 0;

    function onPointerMove(event) {
        var rect = container.getBoundingClientRect();
        if (!rect.width || !rect.height) return;
        pointerTargetX = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        pointerTargetY = ((event.clientY - rect.top) / rect.height) * 2 - 1;
    }

    function onPointerLeave() {
        pointerTargetX = 0;
        pointerTargetY = 0;
    }

    if (!reducedMotion) {
        container.addEventListener("pointermove", onPointerMove);
        container.addEventListener("pointerleave", onPointerLeave);
    }

    /* ---------- 테마 ---------- */
    function applyTokens() {
        tokens = readTokens();
        floorMat.color.set(tokens["--line"]);
        minMarker.material.color.set(tokens["--state-visit"]);
        minMarker.material.emissive.set(tokens["--state-visit"]);
        compareMarker.material.color.set(tokens["--state-compare"]);
        compareMarker.material.emissive.set(tokens["--state-compare"]);
        for (var i = 0; i < bars.length; i += 1) {
            bars[i].cap.material.color.set(tokens["--state-done"]);
            bars[i].cap.material.emissive.set(tokens["--state-done"]);
            bars[i].label.key = "";     /* 라벨 캔버스를 새 토큰으로 다시 그리게 */
        }
        if (frame) setFrame(frame);
        requestRender();
    }

    function onThemeChange() { applyTokens(); }
    document.addEventListener("all:themechange", onThemeChange);

    /* ---------- 재생 루프 ----------
       화면 밖이거나 탭이 숨겨지면 멈춘다 — 랜딩을 열어 둔 채 다른 탭을
       보는 동안 GPU를 계속 돌릴 이유가 없다.

       reduced-motion에서는 루프를 아예 돌리지 않는다. 그 설정에서는 카메라
       드리프트도, 트윈도, 마커 회전도 모두 꺼져 있어 매 프레임 같은 그림이
       나온다 — 같은 그림을 초당 60번 다시 그리는 것은 "애니메이션을 줄여
       달라"는 요청에 대한 답이 아니다. 대신 상태가 바뀔 때 한 장만 그린다. */
    var clock = new THREE.Clock();
    var elapsed = 0;
    var running = false;
    var onScreen = true;
    var pendingRender = 0;

    function frameLoop() {
        var dt = Math.min(clock.getDelta(), 0.05);
        elapsed += dt;
        /* reduced-motion에서는 보간을 건너뛰고 목표값으로 바로 앉힌다 */
        var damp = reducedMotion ? 1 : 1 - Math.pow(0.0025, dt);

        for (var i = 0; i < bars.length; i += 1) {
            var bar = bars[i];

            /* 자리 교대 — 직선으로 미끄러지지 않고 위로 한 번 들렸다 내려온다.
               "누가 어디로 갔는지"가 눈으로 따라진다. */
            if (bar.tween < 1) {
                bar.tween = Math.min(1, bar.tween + (dt * 1000) / SWAP_MS);
                var e = easeInOut(bar.tween);
                var arc = Math.sin(Math.PI * bar.tween);
                bar.group.position.x = bar.fromX + (bar.toX - bar.fromX) * e;
                bar.group.position.y = arc * 0.85;
                bar.group.position.z = arc * 0.5;
                bar.group.rotation.y = arc * 0.55;
            } else {
                bar.group.position.y += (0 - bar.group.position.y) * damp;
                bar.group.position.z += (0 - bar.group.position.z) * damp;
                bar.group.rotation.y += (0 - bar.group.rotation.y) * damp;
            }

            /* 비교 중인 막대는 살짝 떠올라 같은 줄에서 먼저 눈에 걸린다 */
            var lift = bar.state === "compare" ? 0.22 : (bar.state === "min" ? 0.12 : 0);
            bar.mesh.position.y += (lift - bar.mesh.position.y) * damp;
            bar.cap.position.y += (bar.height + 0.045 + lift - bar.cap.position.y) * damp;
            bar.label.sprite.position.y +=
                (bar.height + 0.46 + lift - bar.label.sprite.position.y) * damp;

            bar.mesh.material.color.lerp(bar.targetColor, damp);
            bar.mesh.material.emissive.lerp(bar.targetEmissive, damp);
            bar.mesh.material.emissiveIntensity +=
                (bar.targetEmissiveIntensity - bar.mesh.material.emissiveIntensity) * damp;
        }

        if (frame && bars.length) {
            var bob = reducedMotion ? 0 : Math.sin(elapsed * 3.4) * 0.07;
            if (minMarker.visible) {
                var minBar = barAtIndex(frame.min);
                if (minBar) {
                    minMarker.position.set(minBar.group.position.x,
                        minBar.height + 1.06 + bob, minBar.group.position.z);
                }
            }
            if (compareMarker.visible) {
                var cmpBar = barAtIndex(frame.compare);
                if (cmpBar) {
                    compareMarker.position.set(cmpBar.group.position.x,
                        cmpBar.height + 1.06 - bob, cmpBar.group.position.z);
                    if (!reducedMotion) {
                        compareMarker.rotation.y += dt * 1.9;
                        compareMarker.rotation.x += dt * 0.9;
                    }
                }
            }
        }

        /* 카메라 — 아주 느린 좌우 회전에 포인터 시차를 얹는다.
           reduced-motion에서는 둘 다 끄고 고정된 3/4 시점으로 둔다. */
        pointerX += (pointerTargetX - pointerX) * (1 - Math.pow(0.004, dt));
        pointerY += (pointerTargetY - pointerY) * (1 - Math.pow(0.004, dt));

        var drift = reducedMotion ? 0 : Math.sin(elapsed * 0.22) * 0.30;
        var theta = drift + pointerX * 0.34;
        var phi = 0.30 + (reducedMotion ? 0 : -pointerY * 0.13);
        phi = Math.max(0.1, Math.min(0.72, phi));

        var horizontal = camDistance * Math.cos(phi);
        camera.position.set(
            Math.sin(theta) * horizontal,
            camDistance * Math.sin(phi) + lookAtY,
            Math.cos(theta) * horizontal
        );
        camera.lookAt(0, lookAtY, 0);

        renderer.render(scene, camera);
    }

    function barAtIndex(index) {
        if (!frame || index < 0) return null;
        var value = frame.arr[index];
        for (var i = 0; i < bars.length; i += 1) {
            if (bars[i].value === value) return bars[i];
        }
        return null;
    }

    function start() {
        if (running) return;
        running = true;
        clock.getDelta();       /* 멈춰 있던 시간이 dt로 한 번에 들어오지 않게 */
        renderer.setAnimationLoop(frameLoop);
    }

    function stop() {
        if (!running) return;
        running = false;
        renderer.setAnimationLoop(null);
    }

    /* 루프가 돌지 않을 때(reduced-motion) 한 장만 그린다.
       같은 프레임에 여러 번 불려도 rAF 하나로 합쳐진다. */
    function requestRender() {
        if (running || pendingRender) return;
        pendingRender = window.requestAnimationFrame(function () {
            pendingRender = 0;
            clock.getDelta();
            frameLoop();
        });
    }

    function sync() {
        if (reducedMotion) {
            stop();
            requestRender();
            return;
        }
        if (onScreen && !document.hidden) start();
        else stop();
    }

    var visibilityObserver = null;
    if (window.IntersectionObserver) {
        visibilityObserver = new IntersectionObserver(function (entries) {
            onScreen = entries[0].isIntersecting;
            sync();
        }, { threshold: 0 });
        visibilityObserver.observe(container);
    }
    document.addEventListener("visibilitychange", sync);
    resize();
    sync();

    function destroy() {
        stop();
        if (pendingRender) {
            window.cancelAnimationFrame(pendingRender);
            pendingRender = 0;
        }
        document.removeEventListener("all:themechange", onThemeChange);
        document.removeEventListener("visibilitychange", sync);
        container.removeEventListener("pointermove", onPointerMove);
        container.removeEventListener("pointerleave", onPointerLeave);
        if (resizeObserver) resizeObserver.disconnect();
        else window.removeEventListener("resize", resize);
        if (visibilityObserver) visibilityObserver.disconnect();

        disposeBars();
        barGeo.dispose();
        capGeo.dispose();
        floorGeo.dispose();
        floorMat.dispose();
        shadowMat.dispose();
        floorAlpha.dispose();
        minMarker.geometry.dispose();
        minMarker.material.dispose();
        compareMarker.geometry.dispose();
        compareMarker.material.dispose();
        renderer.dispose();
        if (renderer.domElement.parentNode) {
            renderer.domElement.parentNode.removeChild(renderer.domElement);
        }
    }

    return {
        setValues: setValues,
        setFrame: setFrame,
        destroy: destroy
    };
}

window.AllHero3D = { create: create };
