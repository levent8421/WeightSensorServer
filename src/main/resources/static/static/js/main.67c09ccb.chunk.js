(this.webpackJsonpweight_slot_app=this.webpackJsonpweight_slot_app||[]).push([[0],{135:function(e,t,n){e.exports=n(271)},140:function(e,t,n){},141:function(e,t,n){},201:function(e,t,n){},230:function(e,t,n){},235:function(e,t,n){},236:function(e,t,n){},263:function(e,t,n){},267:function(e,t,n){},270:function(e,t,n){},271:function(e,t,n){"use strict";n.r(t);var a=n(0),o=n.n(a),r=n(19),c=n.n(r),l=(n(140),n(10)),i=n(11),s=n(13),u=n(12),p=(n(141),n(46)),h=n(68),f=n(2),d={showTabBar:!0,globalTitle:"\u91cd\u529b\u8d27\u9053",routerPath:null,dashboardSlots:[],sensors:[],connections:[],user:{name:"MonolithIoT"}},m={},b=function(e,t){m[e]=t};b("tabBar.set",(function(e,t){return Object(f.a)(Object(f.a)({},e),{},{showTabBar:t.data})})),b("router.path.set",(function(e,t){return Object(f.a)(Object(f.a)({},e),{},{routerPath:t.data})})),b("dashboard.slot_data.fetch.async",(function(e,t){return Object(f.a)(Object(f.a)({},e),{},{dashboardSlots:t.data})})),b("title.set",(function(e,t){return Object(f.a)(Object(f.a)({},e),{},{globalTitle:t.data})})),b("sensors.fetch.async",(function(e,t){return Object(f.a)(Object(f.a)({},e),{},{sensors:t.data})})),b("sensor.elabel.toggle",(function(e,t){var n,a=e.sensors,o=t.data.sensorId,r=t.data.hasElabel,c=Object(h.a)(a);try{for(c.s();!(n=c.n()).done;){var l=n.value;if(l.id===o){l.hasElable=r;break}}}catch(i){c.e(i)}finally{c.f()}return Object(f.a)(Object(f.a)({},e),{},{sensors:JSON.parse(JSON.stringify(a))})})),b("connections.fetch.async",(function(e,t){return Object(f.a)(Object(f.a)({},e),{},{connections:t.data})})),b("connection.delete",(function(e,t){var n=t.data.id,a=e.connections.filter((function(e){return e.id!==n}));return Object(f.a)(Object(f.a)({},e),{},{connections:a})}));var v=n(125),E=(window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__?window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({}):p.c)(Object(p.a)(v.a)),y=Object(p.d)((function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:d,t=arguments.length>1?arguments[1]:void 0,n=t.type;if(n in m){var a=m[n];return a(e,t)}return e}),E),O=n(15),j=(n(146),n(47)),g=n.n(j),k=(n(28),n(16)),S=n.n(k),C=(n(201),n(17)),N=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(){return Object(l.a)(this,n),t.apply(this,arguments)}return Object(i.a)(n,[{key:"render",value:function(){var e=this;return this.props.showTabBar?o.a.createElement("div",{className:"appTabBar"},o.a.createElement(g.a,{unselectedTintColor:"#949494",tintColor:"#33A3F4",barTintColor:"white",hidden:!this.props.showTabBar,tabBarPosition:"bottom"},o.a.createElement(g.a.Item,{title:"Dashboard",icon:o.a.createElement(S.a,{type:"check-circle"}),onPress:function(){return e.pushPath("/")}}),o.a.createElement(g.a.Item,{title:"Logs",icon:o.a.createElement(S.a,{type:"check-circle"}),onPress:function(){return e.pushPath("/logs")}}),o.a.createElement(g.a.Item,{title:"Address",icon:o.a.createElement(S.a,{type:"check-circle"}),onPress:function(){return e.pushPath("/address")}}),o.a.createElement(g.a.Item,{title:"Setting",icon:o.a.createElement(S.a,{type:"check-circle"}),onPress:function(){return e.pushPath("/setting")}}))):null}},{key:"pushPath",value:function(e){this.props.history.push({pathname:e})}}]),n}(o.a.Component),T=Object(C.f)(Object(O.b)((function(e,t){return Object(f.a)(Object(f.a)({},t),{},{showTabBar:e.showTabBar})}))(N)),w=(n(49),n(26)),I=n.n(w),D=(n(106),n(30)),P=n.n(D),B=(n(38),n(14)),x=n.n(B),A=(n(45),n(20)),L=n.n(A),_=n(130),U=n.n(_);var M=function(){L.a.hide()},H=function(e){L.a.fail(e)};function Z(e){return e.hideLoading||L.a.loading("Loading",0),new Promise((function(t,n){(function(e){return U()(e)})(e).then((function(e){if(M(),200!==e.status)return H("Http Response Status:".concat(e.status)),void n(e);var a=e.data;if(200!==a.code)return H("Error [".concat(a.code,"]:").concat(a.msg)),void n(e);t(a.data)})).catch((function(e){M();var t=e.toString();H(t),n(e)}))}))}var F=[{id:1,deviceSn:"123456789",address:10,hasElable:!0,slotId:1,slot:{id:1,slotNo:"A-1-1"}},{id:2,deviceSn:"123456789",address:10,hasElable:!0,slotId:1,slot:{id:1,slotNo:"A-1-1"}},{id:3,deviceSn:"123456789",address:10,hasElable:!1,slotId:1,slot:{id:1,slotNo:"A-1-1"}}],K=function(e){return{type:"tabBar.set",data:e}},V=function(e){return{type:"title.set",data:e}},W=function(){return function(e){Z({url:"/api/dashboard/slot-data",method:"get",hideLoading:!0}).then((function(t){var n=[];for(var a in t)t.hasOwnProperty(a)&&n.push(t[a]);var o={type:"dashboard.slot_data.fetch.async",data:n.sort((function(e,t){return e.slotNo.localeCompare(t.slotNo)}))};e(o)}))}},z=function(){return function(e){new Promise((function(e,t){e(F)})).then((function(t){e({type:"sensors.fetch.async",data:t})}))}},J=function(e,t){return function(n){new Promise((function(e,t){e()})).then((function(){n({type:"sensor.elabel.toggle",data:{sensorId:e,hasElabel:t}})}))}},R=function(){return function(e){Z({url:"/api/connection/",method:"get"}).then((function(t){e({type:"connections.fetch.async",data:t})}))}},X=function(e){return function(t){(function(e){return Z({url:"/api/connection/".concat(e),method:"delete"})})(e).then((function(){t({type:"connection.delete",data:{id:e}})}))}},$=(n(230),function(e){return 1!==(e.data&&e.data.toleranceState)}),q={1:"Serial",2:"Network"},G=function(e){return e in q?q[e]:"Unknown [".concat(e,"]")},Q=function(e){return 1===e},Y=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a.props.setTitle("Dashboard"),a}return Object(i.a)(n,[{key:"componentDidMount",value:function(){this.startFetchData()}},{key:"componentWillUnmount",value:function(){clearInterval(this.fetchTimer)}},{key:"startFetchData",value:function(){var e=this;this.props.fetchSlotData(),this.fetchTimer=setInterval((function(){e.props.fetchSlotData()}),3e3)}},{key:"render",value:function(){var e=this.props.slots,t=x.a.Item;return o.a.createElement("div",{className:"dashboard"},o.a.createElement(x.a,{renderHeader:function(){return"SLOT LIST"}},e.map((function(e){return o.a.createElement(t,{key:e.slotNo},o.a.createElement(P.a,{className:$(e)?"warn":""},o.a.createElement(P.a.Header,{title:e.slotNo,extra:e.sku&&e.sku.name}),o.a.createElement(P.a.Body,null,o.a.createElement(I.a,{className:"slotCard",justify:"center"},o.a.createElement("div",{className:"count"},o.a.createElement("span",{className:$(e)?"warn value":"value"},e.data&&e.data.count),o.a.createElement("span",{className:"unit"},"pis")),o.a.createElement("div",{className:Q(e.data&&e.data.weightState)?"weight":"weight warn"},o.a.createElement("span",{className:$(e)?"warn value":"value"},Q(e.data&&e.data.weightState)?"":"~",e.data&&(e.data.weight/1e3).toFixed(3)),o.a.createElement("span",{className:"unit"},"kg")))),o.a.createElement(P.a.Footer,{content:e.sku&&e.sku.skuNo})))}))))}}]),n}(a.Component),ee=Object(O.b)((function(e,t){return Object(f.a)(Object(f.a)({},t),{},{slots:e.dashboardSlots})}),(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{fetchSlotData:function(){return e(W.apply(void 0,arguments))},setTitle:function(){return e(V.apply(void 0,arguments))}})}))(Y),te=n(29),ne=(n(116),n(65)),ae=n.n(ne),oe=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a.props.setTitle("Sensor Setting"),a.props.setTabBarState(!1),a}return Object(i.a)(n,[{key:"render",value:function(){var e=this,t=this.props.sensors;return o.a.createElement("div",{className:"slotSetting"},o.a.createElement(x.a,{renderHeader:function(){return"Sensors"}},t.map((function(t){return o.a.createElement(x.a.Item,{key:t.id},o.a.createElement(P.a,null,o.a.createElement(P.a.Header,{title:"Address:".concat(t.address),extra:t.deviceSn}),o.a.createElement(P.a.Body,null,o.a.createElement(I.a,{justify:"between"},o.a.createElement("span",null,"ELabel"),o.a.createElement(ae.a,{checked:t.hasElable,onChange:function(n){return e.toggleElabel(t,n)}}))),o.a.createElement(P.a.Footer,{content:"Slot:[".concat(t.slot&&t.slot.slotNo,"]"),extra:t.slot&&t.slot.id})))}))))}},{key:"componentDidMount",value:function(){this.props.fetchSensors()}},{key:"toggleElabel",value:function(e,t){this.props.toggleSensorElable(e.id,t)}}]),n}(a.Component),re=Object(O.b)((function(e,t){return Object(f.a)(Object(f.a)({},t),{},{sensors:e.sensors})}),(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{fetchSensors:function(){return e(z.apply(void 0,arguments))},toggleSensorElable:function(){return e(J.apply(void 0,arguments))},setTitle:function(){return e(V.apply(void 0,arguments))},setTabBarState:function(){return e(K.apply(void 0,arguments))}})}))(oe),ce=(n(232),n(40)),le=n.n(ce),ie=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a.props.setTitle("Setting"),a}return Object(i.a)(n,[{key:"componentDidMount",value:function(){this.props.setTabBarState(!0)}},{key:"render",value:function(){var e=this;return o.a.createElement("div",{className:"setting"},o.a.createElement(x.a,null,o.a.createElement(x.a.Item,{onClick:function(){return e.go("/setting/sensor")}},o.a.createElement(le.a,null),o.a.createElement(I.a,{justify:"between"},o.a.createElement("span",null,"Sensor Setting"),o.a.createElement(S.a,{type:"right"})),o.a.createElement(le.a,null)),o.a.createElement(x.a.Item,{onClick:function(){return e.go("/setting/slot")}},o.a.createElement(le.a,null),o.a.createElement(I.a,{justify:"between"},o.a.createElement("span",null,"Slot Setting"),o.a.createElement(S.a,{type:"right"})),o.a.createElement(le.a,null)),o.a.createElement(x.a.Item,{onClick:function(){return e.go("/setting/connection")}},o.a.createElement(le.a,null),o.a.createElement(I.a,{justify:"between"},o.a.createElement("span",null,"Connection Setting"),o.a.createElement(S.a,{type:"right"})),o.a.createElement(le.a,null))))}},{key:"go",value:function(e){this.props.history.push({pathname:e})}}]),n}(a.Component),se=Object(C.f)(Object(O.b)(null,(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{setTitle:function(){return e(V.apply(void 0,arguments))},setTabBarState:function(){return e(K.apply(void 0,arguments))}})}))(ie)),ue=(n(117),n(66)),pe=n.n(ue),he=(n(235),n(236),function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(){return Object(l.a)(this,n),t.apply(this,arguments)}return Object(i.a)(n,[{key:"render",value:function(){var e=this;return o.a.createElement("div",{className:"floatButton",onClick:function(){return e.onClick()}},o.a.createElement(S.a,{type:this.props.iconType,className:"icon"}))}},{key:"onClick",value:function(){this.props.onClick&&this.props.onClick()}}]),n}(a.Component)),fe=["Refresh","Do Zero All","Cancel"],de=x.a.Item,me=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={slots:[]},a}return Object(i.a)(n,[{key:"componentDidMount",value:function(){this.props.setTabBarState(!1),this.props.setTitle("Slot Settings"),this.fetchSlots()}},{key:"render",value:function(){var e=this,t=this.state.slots;return o.a.createElement("div",{className:"slotSetting"},o.a.createElement(x.a,{renderHeader:function(){return"Slot List"}},t.map((function(t){return o.a.createElement(de,{key:t.id,extra:o.a.createElement(S.a,{type:"right"}),onClick:function(){return e.toSlotDetail(t)}},o.a.createElement(I.a,{justify:"between"},o.a.createElement("span",{className:"slotNo"},t.slotNo),o.a.createElement("span",{className:"name"},t.skuName)))}))),o.a.createElement(he,{iconType:"ellipsis",onClick:function(){return e.showOperationSheet()}}))}},{key:"showOperationSheet",value:function(){var e=this;pe.a.showActionSheetWithOptions({options:fe,title:"Operations",cancelButtonIndex:fe.length-1,destructiveButtonIndex:1},(function(t){switch(t){case 0:e.fetchSlots();break;case 1:e.doZeroAll()}}))}},{key:"toSlotDetail",value:function(e){this.props.history.push({pathname:"/setting/slot-detail/".concat(e.id)})}},{key:"doZeroAll",value:function(){Z({url:"/api/slot/zero-all",method:"post"}).then((function(){L.a.show("All Zeroed!")}))}},{key:"fetchSlots",value:function(){var e=this;Z({url:"/api/slot/",method:"get"}).then((function(t){e.setState({slots:t})}))}}]),n}(a.Component),be=Object(C.f)(Object(O.b)(null,(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{setTabBarState:function(){return e(K.apply(void 0,arguments))},setTitle:function(){return e(V.apply(void 0,arguments))}})}))(me)),ve=(n(237),n(131)),Ee=n.n(ve),ye=(n(242),n(88)),Oe=n.n(ye),je=(n(122),n(31)),ge=n.n(je),ke=(n(83),n(50)),Se=n.n(ke),Ce=(n(263),[{label:"Serial",value:1},{label:"Network",value:2}]),Ne=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={createDialogVisible:!1,create:{type:null,target:""},serialPorts:[]},a.props.setTitle("Connection Setting"),a}return Object(i.a)(n,[{key:"componentDidMount",value:function(){this.props.fetchConnection(),this.props.setTabBarState(!1)}},{key:"render",value:function(){var e=this,t=x.a.Item,n=this.props.connections,a=this.state.createDialogVisible,r=this.state.create;return o.a.createElement("div",null,o.a.createElement(x.a,{renderHeader:function(){return"Connections"}},n.map((function(n){return o.a.createElement(t,{key:n.id},o.a.createElement(I.a,{justify:"between",className:"connectionItem"},o.a.createElement("span",{className:"type"},G(n.type)),o.a.createElement("span",{className:"target"},n.target),o.a.createElement(S.a,{className:"deleteButton",type:"cross-circle",onClick:function(){return e.deleteConnection(n)}})))})),o.a.createElement(t,{key:"createButton"},o.a.createElement(Se.a,{type:"primary",onClick:function(){return e.showCreateDialog()}},"Create New"))),o.a.createElement(Ee.a,{visible:a,transparent:!0,title:"Create A Connection",footer:[{text:"Cancel",onPress:function(){return e.setState({createDialogVisible:!1})}},{text:"Create",onPress:function(){return e.createConnection()}}],maskClosable:!0},o.a.createElement(x.a,{title:"Connection"},o.a.createElement(Oe.a,{data:Ce,title:"Connection Type",cols:1,extra:"Choose",onChange:function(t){return e.setCreateType(t)}},o.a.createElement(x.a.Item,null,o.a.createElement(ge.a,{value:r.type&&G(r.type),disabled:!0,placeholder:"Type"}))),function(){var t=o.a.createElement(x.a.Item,{key:"target"},o.a.createElement(ge.a,{placeholder:"Connection Target",onChange:function(t){return e.setCreateTarget(t)},value:r.target,disabled:1===e.state.create.type}));return 1===r.type?o.a.createElement(Oe.a,{data:e.state.serialPorts,extra:"Choose",title:"Serial Port List",cols:1,onChange:function(t){return e.setCreateTargetSerial(t)}},t):t}())))}},{key:"deleteConnection",value:function(e){this.props.deleteConnection(e.id)}},{key:"showCreateDialog",value:function(){this.setState({createDialogVisible:!0})}},{key:"createConnection",value:function(){var e,t=this;(e=this.state.create,Z({url:"/api/connection/",method:"put",data:e})).then((function(){t.props.fetchConnection(),t.setState({create:{},createDialogVisible:!1})}))}},{key:"setCreateType",value:function(e){var t=this;e.length<1&&L.a.show("Please Choose A Connection Type!");var n=e[0],a={type:n,target:""};this.setState({create:a}),1===n&&this.state.serialPorts.length<=0&&Z({url:"/api/serial/scan",method:"get"}).then((function(e){var n,a=[],o=Object(h.a)(e);try{for(o.s();!(n=o.n()).done;){var r=n.value;a.push({label:r,value:r})}}catch(c){o.e(c)}finally{o.f()}t.setState({serialPorts:a})}))}},{key:"setCreateTarget",value:function(e){var t=Object(f.a)(Object(f.a)({},this.state.create),{},{target:e});this.setState({create:t})}},{key:"setCreateTargetSerial",value:function(e){if(e){var t=e[0],n=Object(f.a)(Object(f.a)({},this.state.create),{},{target:t});this.setState({create:n})}else L.a.show("Please Choose A Serial Port!")}}]),n}(a.Component),Te=Object(O.b)((function(e,t){return Object(f.a)(Object(f.a)({},t),{},{connections:e.connections})}),(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{fetchConnection:function(){return e(R.apply(void 0,arguments))},setTitle:function(){return e(V.apply(void 0,arguments))},deleteConnection:function(){return e(X.apply(void 0,arguments))},setTabBarState:function(){return e(K.apply(void 0,arguments))}})}))(Ne),we=(n(264),n(132)),Ie=n.n(we),De=["Do Zero","Delete","Cancel"],Pe=x.a.Item,Be=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={slot:{}},a.slotId=a.props.match.params.id,a}return Object(i.a)(n,[{key:"componentDidMount",value:function(){this.props.setTitle("".concat(this.slotId," Settings")),this.fetchSlotInfo()}},{key:"fetchSlotInfo",value:function(){var e,t=this;(e=this.slotId,Z({url:"/api/slot/".concat(e),method:"get"})).then((function(e){t.setState({slot:e}),t.props.setTitle("".concat(e.slotNo," Settings"))}))}},{key:"render",value:function(){var e=this,t=this.state.slot;return o.a.createElement("div",{className:"slotDetail"},o.a.createElement(x.a,{renderHeader:function(){return"Slot Info"}},o.a.createElement(Pe,{key:"slotNo"},o.a.createElement(ge.a,{placeholder:"Slot No",value:t.slotNo,onChange:function(t){return e.setUpdateSlotProp({slotNo:t})}},"SlotNo")),o.a.createElement(Pe,{key:"SkuName"},o.a.createElement(ge.a,{placeholder:"Sku Name",value:t.skuName,onChange:function(t){return e.setUpdateSlotProp({skuName:t})}},"SKUName")),o.a.createElement(Pe,{key:"skuNo"},o.a.createElement(ge.a,{placeholder:"SKU No",value:t.skuNo,onChange:function(t){return e.setUpdateSlotProp({skuNo:t})}},"SKUNo")),o.a.createElement(Pe,{key:"skuApw"},o.a.createElement(ge.a,{placeholder:"SKU Apw",value:t.skuApw,onChange:function(t){return e.setUpdateSlotProp({skuApw:t})}},"SKUApw")),o.a.createElement(Pe,{key:"skuTolerance"},o.a.createElement(ge.a,{placeholder:"SKU Apw",value:t.skuTolerance,onChange:function(t){return e.setUpdateSlotProp({skuTolerance:t})}},"SKUTolerance"))),o.a.createElement(Ie.a,null,o.a.createElement(Se.a,{type:"primary",onClick:function(){return e.applyModify()}},"Apply Modify")),o.a.createElement(x.a,{renderHeader:function(){return"ELabel"}},o.a.createElement(x.a.Item,{key:"hasELabel",extra:o.a.createElement(ae.a,{checked:t.hasElabel,onChange:function(t){return e.toggleELabel(t)}})},"Enable ELabel")),o.a.createElement(he,{iconType:"ellipsis",onClick:function(){return e.openOperation()}}))}},{key:"openOperation",value:function(){var e=this;pe.a.showActionSheetWithOptions({title:"Operations",options:De,destructiveButtonIndex:1,cancelButtonIndex:De.length-1},(function(t){var n=De[t];"Do Zero"===n?e.doZero():"Delete"===n&&L.a.show("Unable To Delete!")}))}},{key:"doZero",value:function(){var e,t=this.state.slot;(e=t.slotNo,Z({url:"/api/slot/".concat(e,"/zero"),method:"post"})).then((function(){L.a.show("Do Zero Success!")}))}},{key:"setUpdateSlotProp",value:function(e){var t=Object(f.a)(Object(f.a)({},this.state.slot),e);this.setState({slot:t})}},{key:"applyModify",value:function(){var e;(e=this.state.slot,Z({url:"/api/slot/".concat(e.id),method:"post",data:e})).then((function(){L.a.show("Apply Success!")}))}},{key:"toggleELabel",value:function(e){var t,n=this;(t={id:this.slotId,hasELabel:e},Z({url:"/api/slot/".concat(t.id,"/has-e-label"),method:"post",data:{hasElabel:t.hasELabel}})).then((function(){L.a.show("Toggle ELabel Success!"),n.fetchSlotInfo()}))}}]),n}(a.Component),xe=Object(C.f)(Object(O.b)(null,(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{setTitle:function(){return e(V.apply(void 0,arguments))}})}))(Be)),Ae=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a}return Object(i.a)(n,[{key:"render",value:function(){return o.a.createElement(te.a,null,o.a.createElement(C.c,null,o.a.createElement(C.a,{path:"/setting/",component:se,exact:!0}),o.a.createElement(C.a,{path:"/setting/sensor",component:re,exact:!0}),o.a.createElement(C.a,{path:"/setting/slot",component:be,exact:!0}),o.a.createElement(C.a,{path:"/setting/connection",component:Te,exact:!0}),o.a.createElement(C.a,{path:"/setting/slot-detail/:id",component:xe,exact:!0})))}}]),n}(a.Component),Le=Object(O.b)(null,(function(e,t){return Object(f.a)({},t)}))(Ae),_e=(n(124),n(67)),Ue=n.n(_e),Me=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a.props.setTitle("Config Address"),a}return Object(i.a)(n,[{key:"render",value:function(){return o.a.createElement("div",null,o.a.createElement(Ue.a,{img:o.a.createElement(S.a,{type:"check-circle",size:"lg",style:{fill:"#1F90E6"}}),title:"\u5f00\u53d1\u4e2d",message:"\u5f00\u53d1\u4e2d"}))}},{key:"componentDidMount",value:function(){L.a.info("\u5f00\u53d1\u4e2d",1,null,!1)}}]),n}(a.Component),He=Object(O.b)(null,(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{setTitle:function(){return e(V.apply(void 0,arguments))}})}))(Me),Ze=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a.props.setTitle("Logs"),a}return Object(i.a)(n,[{key:"render",value:function(){return o.a.createElement("div",null,o.a.createElement(Ue.a,{img:o.a.createElement(S.a,{type:"check-circle",size:"lg",style:{fill:"#1F90E6"}}),title:"\u5f00\u53d1\u4e2d",message:"\u5f00\u53d1\u4e2d"}))}},{key:"componentDidMount",value:function(){L.a.info("\u5f00\u53d1\u4e2d",1,null,!1)}}]),n}(a.Component),Fe=Object(O.b)(null,(function(e,t){return Object(f.a)(Object(f.a)({},t),{},{setTitle:function(){return e(V.apply(void 0,arguments))}})}))(Ze),Ke=(n(267),function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(e){var a;return Object(l.a)(this,n),(a=t.call(this,e)).state={},a}return Object(i.a)(n,[{key:"render",value:function(){return o.a.createElement("div",{className:"appContent"},o.a.createElement("div",{className:"headerMask"}),o.a.createElement(C.c,null,o.a.createElement(C.a,{path:"/",component:ee,exact:!0}),o.a.createElement(C.a,{path:"/setting**",component:Le,exact:!0}),o.a.createElement(C.a,{path:"/address",component:He,exact:!0}),o.a.createElement(C.a,{path:"/logs",component:Fe,exact:!0})),o.a.createElement("div",{className:"tabBarMask"}))}}]),n}(a.Component)),Ve=(n(268),n(133)),We=n.n(Ve),ze=(n(270),function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(){return Object(l.a)(this,n),t.apply(this,arguments)}return Object(i.a)(n,[{key:"render",value:function(){var e=this,t=this.props.title;return o.a.createElement("div",{className:"appHeader"},o.a.createElement(We.a,{leftContent:o.a.createElement(S.a,{type:"left",onClick:function(){return e.onBackClick()}}),rightContent:o.a.createElement(S.a,{type:"ellipsis"}),mode:"light"},t))}},{key:"onBackClick",value:function(){this.props.history.goBack()}}]),n}(a.Component)),Je=Object(C.f)(Object(O.b)((function(e,t){return Object(f.a)(Object(f.a)({},t),{},{title:e.globalTitle})}))(ze)),Re=n(134),Xe=Object(Re.a)(),$e=function(e){var t=function(e){return{type:"router.path.set",data:e}}({pathname:e.pathname,search:e.search,hash:e.hash});y.dispatch(t)};Xe.listen((function(e){$e(e.location)})),$e(Xe.location);var qe=Xe,Ge=function(e){Object(s.a)(n,e);var t=Object(u.a)(n);function n(){return Object(l.a)(this,n),t.apply(this,arguments)}return Object(i.a)(n,[{key:"render",value:function(){return o.a.createElement(te.a,{history:qe},o.a.createElement(O.a,{store:y},o.a.createElement("div",{className:"app-content"},o.a.createElement(Je,null),o.a.createElement(Ke,null),o.a.createElement(T,null))))}}]),n}(o.a.Component);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));c.a.render(o.a.createElement(Ge,null),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))}},[[135,1,2]]]);
//# sourceMappingURL=main.67c09ccb.chunk.js.map