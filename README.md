# SmileGuns-sml.1.1.2
※まだM4A1しか使用できません
## 確認されているバグ一覧
・ インベントリを開いてオフハンドに銃を切り替えようとすると透明の銃が設置される（増殖バグ）  
・ リロード中に銃を捨てて新たな銃を手に入れるとそのままリロードが続行される  
・ 防具無効  
・ ドア&トラップドアが開いてるのに弾が通らない  
・ 通常のクロスボウでADSモードになってしまう  
（書いていいのか、これ。）
## 実装されている銃
・ M4A1
## 今後実装予定&実装してほしいもの
・ コマンドをコマブロで使えるようにしてほしい  
・ 他の銃が欲しい  
・ デバッグしやがれ  
・ アタッチメントあったらいいな  

## アップデート内容
### sml.1.1.1 (2020 12/14)
・ バージョンの表記法を「sml-〇.△.□」から「sml.〇.△.□」に変更  
・ バグの修正（修正済みバグ一覧を参照）  
・ コマンドのアップデート（コマンドのアップデート内容を参照）  
 ### sml.1.1.2:
・ 横方向のリコイルを追加
・ 弾のデフォルト散弾角度を10から7に変更
・ バグの修正（修正済みバグ一覧を参照）

## コマンドのアップデート内容
### sml.1.1.1
・ /gun get...を/gun give...に変更  
・ /gun give...でセレクターが見つからなかった場合に発生する例外をPlayerNotFoundExceptionに変更  
・ コマンドにメッセージを追加  

## 修正済みバグ一覧
### sml.1.1.1
・ 感圧版に乗ると大量に連射される  
・ スペクテイターで銃が撃ててしまう  
・ スペクテイター&クリエイティブでもダメージが入る  
・ ダメージアニメーションがない  
・ クロスボウ＆石剣が使えない  
・ 跳弾のサウンドの有効条件  
