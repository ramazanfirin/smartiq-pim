import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IBasket, Basket } from '../basket.model';
import { BasketService } from '../service/basket.service';
import { BasketStatus } from 'app/entities/enumerations/basket-status.model';

@Component({
  selector: 'jhi-basket-update',
  templateUrl: './basket-update.component.html',
})
export class BasketUpdateComponent implements OnInit {
  isSaving = false;
  basketStatusValues = Object.keys(BasketStatus);

  editForm = this.fb.group({
    id: [],
    createDate: [null, [Validators.required]],
    status: [null, [Validators.required]],
    totalCost: [null, [Validators.required]],
  });

  constructor(protected basketService: BasketService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ basket }) => {
      this.updateForm(basket);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const basket = this.createFromForm();
    if (basket.id !== undefined) {
      this.subscribeToSaveResponse(this.basketService.update(basket));
    } else {
      this.subscribeToSaveResponse(this.basketService.create(basket));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBasket>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(basket: IBasket): void {
    this.editForm.patchValue({
      id: basket.id,
      createDate: basket.createDate,
      status: basket.status,
      totalCost: basket.totalCost,
    });
  }

  protected createFromForm(): IBasket {
    return {
      ...new Basket(),
      id: this.editForm.get(['id'])!.value,
      createDate: this.editForm.get(['createDate'])!.value,
      status: this.editForm.get(['status'])!.value,
      totalCost: this.editForm.get(['totalCost'])!.value,
    };
  }
}
