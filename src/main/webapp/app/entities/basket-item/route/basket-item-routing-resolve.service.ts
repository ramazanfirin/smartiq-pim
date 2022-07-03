import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBasketItem, BasketItem } from '../basket-item.model';
import { BasketItemService } from '../service/basket-item.service';

@Injectable({ providedIn: 'root' })
export class BasketItemRoutingResolveService implements Resolve<IBasketItem> {
  constructor(protected service: BasketItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBasketItem> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((basketItem: HttpResponse<BasketItem>) => {
          if (basketItem.body) {
            return of(basketItem.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BasketItem());
  }
}
